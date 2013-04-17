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
    	
    	System.out.println(" ServerMRR initalized!");
    	
    	/* Loop forever waiting on user input and process that input */
    	while (true) {
    		System.out.print("->> ");
    		inputString = inputScan.nextLine();
    		
    		if (inputString.equals("")) {
                continue;
    		} else if (inputString.equals("jobs")) {
                job = master.getJobs().iterator();
    			if (master.getJobs().size() == 0) {
    				System.out.println("No Running Jobs");
    			} else {
    				/* Print out all local processes (in local processes set) */
    				System.out.println("Running Jobs");
    				System.out.println("JobID                     Name                    Host:Port                   Tasks");
    	    		while (job.hasNext()) {
    	                JobEntry t = job.next();
    					System.out.println(t.toString());
    				}
    			}
    		} else if (inputString.equals("quit")) {
    			System.out.println("Goodbye!");
    			System.exit(0);
    		} else if (inputString.equals("power")) {
    			System.out.println("Current System Power (Active Nodes / Total Nodes): " + 
    					Integer.toString(this.master.getCurrentPower()) + " (" + Integer.toString(this.master.getNodes()) + 
    					"/" + Integer.toString(this.master.getTotalNodes()) + ")");
    		} else if (inputString.contains("stop") && (inputString.split(" ").length >= 2)) {
    			if (this.master.stopJob(Long.parseLong(inputString.split(" ")[1]), new ServerTerminationException())) {
    				System.out.println("Terminate job " + Integer.parseInt(inputString.split(" ")[1]));
    			} else {
    				System.out.println("Failed to terminate job " + Integer.parseInt(inputString.split(" ")[1]));
    			}
    		} else {
    			System.out.println(inputString + ": Command Not Found");
    		}
    	}
	}
}