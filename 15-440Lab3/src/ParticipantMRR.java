import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
            System.exit(-1);
        }
        
        try {
            this.socket = new Socket(this.host,this.port);
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            // Couldn't connect to master, no error because this process was created remotely
            System.exit(-1);
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
        	
        	// TODO: add jobs to the queue when we get them */
        }
    }
    
    public JobEntry getNextJob() throws Exception {
    	return this.jobs.take();
    }
    
    
}