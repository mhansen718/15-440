import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class ParticipantMRR {
    
    private int processors;
    private String host;
    private int port;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private LinkedBlockingQueue<TaskEntry> tasks;
    private HashSet<TaskID> completedTasks;
    private HashSet<Thread> minions;
    private Semaphore completedTasksProtect;
    private ConcurrentLinkedQueue<JobEntry> newJobs;
    
    public ParticipantMRR() {
    	super();
    	this.minions = new HashSet<Thread>();
    	this.completedTasks = new HashSet<TaskID>();
    	this.completedTasksProtect = new Semaphore(1);
    }
    
    public void main(String args[]) {
    	Iterator<Thread> minion;
    	Thread t;
    	int id;
        ParticipantStatus status = null;
    	
    	/* First order of business, KILL ALL OTHER PARTICIPANTS ON THIS SYSTEM,
    	 * command credits to the internet */
    	try {
			Runtime.getRuntime().exec("kill -9 `ps ax | grep 'java ClientMRR' | awk '{print $1}'");
		} catch (IOException e3) {
			/* Failed to take over this node, maybe..... we'll just have to try again later */
			System.exit(-1);
		}
    	
        this.processors = Runtime.getRuntime().availableProcessors();
        
        this.host = args[0];
        
        try {
            this.port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            // Our script somehow failed to give a valid integer port... What!?
        	System.out.println(":(");
            System.exit(-1);
        }
        
        try {
            this.socket = new Socket(this.host,this.port);
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            // Couldn't connect to master, no error because this process was created remotely
            // System.exit(-1);
        }
        
        status = new ParticipantStatus();
        status.power = this.processors;
        
        try {
            this.out.writeObject(status);
            this.out.close();
        } catch (IOException e) {
        
        }
        
        Thread newJobListener = new Thread(new ParticipantListenerMRR(this, Integer.parseInt(args[2])));
        newJobListener.start();
        
        RandomAccessFile mine = null;
		try {
			mine = new RandomAccessFile(System.getProperty("user.dir") + "/hi." + InetAddress.getLocalHost().getHostName() + ".txt", "rw");
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnknownHostException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
        
        /* Run the main loop */
        while (true) {
        	/* check the minions, removing the dead and adding more if needed */
        	this.processors = Runtime.getRuntime().availableProcessors();
        	
        	/* Remove the dead minions from the list */
        	minion = this.minions.iterator();
        	while (minion.hasNext()) {
        		t = minion.next();
        		if (!(t.isAlive())) {
        			minion.remove();
        		}
        	}
        	
        	/* Add new minions if there's room */
        	if (this.processors > this.minions.size()) {
        		id = this.minions.size() + 1;
        		for (; id <= this.processors; id++) {
        			t = new Thread(new ParticipantMinionMRR(this, id));
        			t.start();
        			this.minions.add(t);
        		}
        	}
        	
        	try {
				mine.write("Hello\n".getBytes());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
            try {
                in = new ObjectInputStream(this.socket.getInputStream());
                status = in.readObject();
            } catch (IOException e) {
                continue;
            }
            
            //TODO: process new tasks
        }
    }
    
    public void completeTask(TaskID id) {
    	/* Acquire the semphore and add the task to the completed tasks list */
    	this.completedTasksProtect.acquireUninterruptibly();
    	this.completedTasks.add(id);
    	this.completedTasksProtect.release();
    }
    
    public TaskEntry getNextTask() throws Exception {
    	/* Takes the next task off the queue */
    	return this.tasks.take();
    }
    
    public void addNewJob(JobEntry job) {
    	/* Add a new job to the queue */
    	this.newJobs.add(job);
    	return;
    }
}