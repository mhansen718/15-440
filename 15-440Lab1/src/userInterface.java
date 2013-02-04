import java.util.Scanner;


public class userInterface implements Runnable {

    private ProcessManager manager = null;

	public userInterface(ProcessManager manager) {
		super();
		this.manager = manager;
	}

	public void run() {
    	/* Variables to be used for user input */
    	String inputString = new String();
    	Scanner inputScan = new Scanner(System.in);
    	Thread[] processesAsThreads;
        ThreadGroup processes;
    	
    	/* Loop forever waiting on user input and process that input */
    	while (true) {
    		System.out.print("->> ");
    		inputString = inputScan.nextLine();
    		
    		if (inputString == "ps") {
                processes = manager.getProcesses();
    			if (processes.activeCount() == 0) {
    				System.out.println("No Running Local Processes");
    			} else {
    				/* Print out all local processes (in local processes group) */
    				processesAsThreads = new Thread[processes.activeCount()];
    				processes.enumerate(processesAsThreads);
    				System.out.println(processes.getName());
    				for (Thread t: processesAsThreads) {
    					System.out.println(ProcessManager.convertFromThreadName(t.getName()));
    				}
    			}
    		} else if (inputString == "quit") {
    			System.out.println("Goodbye!");
    			return;
    		} else {
                                                                                    //TODO: Send to Master for processing
    			if (inputString.contains("#")) {
    				System.out.println("Error: Class/Arguments cannot contain '#' character");
    			} else {
    				manager.insertToBuffer("#" + inputString);
    			}
    		}
    	}
	}
}