import java.util.Iterator;
import java.util.Scanner;


public class UserInterface implements Runnable {

    private MasterMRR master = null;

	public UserInterface(MasterMRR master) {
		super();
		this.master = master;
	}

	public void run() {
    	/* Variables to be used for user input */
    	String inputString = new String();
    	Scanner inputScan = new Scanner(System.in);
    	Iterator<JobEntry> job = null;
    	
    	/* Loop forever waiting on user input and process that input */
    	while (true) {
    		System.out.print("->> ");
    		inputString = inputScan.nextLine();
    		
    		if (inputString.equals("")) {
                continue;
    		} else if (inputString.equals("jobs")) {
                job = master.getJobs().iterator();
    			if (master.getJobs().size() == 0) {
    				System.out.println("No Running Local Processes");
    			} else {
    				/* Print out all local processes (in local processes set) */
    				System.out.println("Local Running Processes");
    	    		while (job.hasNext()) {
    	                JobEntry t = job.next();
    					System.out.println(t.toString());
    				}
    			}
    		} else if (inputString.equals("quit")) {
    			System.out.println("Goodbye!");
    			return;
    		} else {
    			System.out.println(inputString + ": Command Not Found");
    		}
    	}
	}
}