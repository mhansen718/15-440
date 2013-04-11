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
import java.util.concurrent.BlockingQueue;

public class ParticipantMRR {
    
    private int processors;
    private String host;
    private int port;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private BlockingQueue<JobEntry> jobs;
    private HashSet<Thread> minions;
    
    public ParticipantMRR() {
    	super();
    	this.minions = new HashSet<Thread>();
    }
    
    public void main(String args[]) {
    	Iterator<Thread> minion;
    	Thread t;
    	int id;
    	
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
            this.in = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            // Couldn't connect to master, no error because this process was created remotely
            // System.exit(-1);
        }
        
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
        	System.out.println("hi");
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
        	// TODO: add jobs to the queue when we get them */
        }
    }
    
    public JobEntry getNextJob() throws Exception {
    	return this.jobs.take();
    }
    
    
}