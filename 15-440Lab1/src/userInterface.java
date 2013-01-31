import java.util.Scanner;


public class userInterface implements Runnable {
	
	private ThreadGroup processes;

	public userInterface(ThreadGroup processes) {
		super();
		this.processes = processes;
	}

	public void run() {
    	/* Variables to be used for user input */
    	String inputString = new String();
    	Scanner inputScan = new Scanner(System.in);
    	Thread[] processesAsThreads;
    	
    	/* Loop forever waiting on user input and process that input */
    	while (true) {
    		System.out.print("->> ");
    		inputString = inputScan.nextLine();
    		
    		if (inputString == "ps") {
    			if (processes.activeCount() == 0) {
    				System.out.println("No Running Local Processes");
    			}
    			else {
    				/* Print out all local processes (in local processes group) */
    				processesAsThreads = new Thread[processes.activeCount()];
    				processes.enumerate(processesAsThreads);
    				System.out.println(processes.getName());
    				for (Thread t: processesAsThreads) {
    					System.out.println(((MigratableProcess) t).toString());
    				}
    			}
    		}
    		else if (inputString == "quit") {
    			System.out.println("Goodbye!");
    			return;
    		}
    		else {
    			//TODO: Send to Master for processing
    		}
    	}
	}
}