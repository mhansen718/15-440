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
        Iterator<TaskID> idIter;
        Iterator<JobEntry> jobIter;
        Iterator<TaskEntry> taskIter;
    	Thread t;
    	int id;
        ParticipantStatus status = null;
        ParticipantStatus confirmation = null;
    	
    	/* First order of business, KILL ALL OTHER PARTICIPANTS ON THIS SYSTEM,
    	 * command credits to the internet */
    	try {
			Runtime.getRuntime().exec("kill -9 `ps ax | grep 'java ClientMRR' | awk '{print $1}'");
		} catch (IOException e3) {
			/* Failed to take over this node, maybe..... we'll just have to try again later */
			System.exit(-1);
		}
    	
    	if (args.length != 3 ) {
    		System.out.println("java ClientMRR [master host] [master port] [local job listen port]");
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
            System.exit(-1);
        }
        
        status = new ParticipantStatus();
        status.power = this.processors;
        
        try {
            this.out.writeObject(status);
            this.socket.close();
        } catch (IOException e) {
            // Failed to phone home properly, may as well wait to be remade
            System.exit(-1);
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
                this.socket = new Socket(this.host,this.port);
                in = new ObjectInputStream(this.socket.getInputStream());
                status = (ParticipantStatus) in.readObject();
            } catch (Exception e) {
                System.exit(-1);
            }
            
            taskIter = (status.newTasks.values()).iterator();
            while (taskIter.hasNext()) {
                tasks.offer(taskIter.next());
            }
            
            status.completedTasks = flushCompleted();
            status.newJobs = flushNewJobs();
            status.newTasks = null;
            
            try {
                out = new ObjectOutputStream(this.socket.getOutputStream());
                out.writeObject(status);
                out.close();
                in.close();
                this.socket.close();
            } catch (IOException e) {
                continue;
            }
            
            try {
                confirmation = (ParticipantStatus) in.readObject();
            } catch (Exception e) {
                // Put everything back in the queues, we aren't sure the master knows
                idIter = status.completedTasks.iterator();
                while (idIter.hasNext()) {
                    completeTask(idIter.next());
                }
                jobIter = status.newJobs.iterator();
                while (jobIter.hasNext()) {
                    addNewJob(jobIter.next());
                }
                continue;
            }
        }
    }
    
    public void completeTask(TaskID id) {
    	/* Acquire the semphore and add the task to the completed tasks list */
    	this.completedTasksProtect.acquireUninterruptibly();
    	this.completedTasks.add(id);
    	this.completedTasksProtect.release();
    }
    
    private HashSet<TaskID> flushCompleted() {
        HashSet<TaskID> tasks = new HashSet<TaskID>();
        Iterator<TaskID> iter;
        this.completedTasksProtect.acquireUninterruptibly();
        iter = this.completedTasks.iterator();
        while (iter.hasNext()) {
            tasks.add(iter.next());
            iter.remove();
        }
    	this.completedTasksProtect.release();
        return tasks;
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
    
    private HashSet<JobEntry> flushNewJobs() {
        HashSet<JobEntry> jobs = new HashSet();
        JobEntry j = this.newJobs.poll();
        while (j != null) {
            jobs.add(j);
            j = this.newJobs.poll();
        }
        return jobs;
    }
}