import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;


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
        Set<MigratableThread> processes;
        Iterator<MigratableThread> iterator;
    	
    	/* Loop forever waiting on user input and process that input */
    	while (true) {
    		System.out.print("->> ");
    		inputString = inputScan.nextLine();
    		if (inputString.equals("")) {
                continue;
    		} else if (inputString.equals("ps")) {
                processes = manager.getProcesses();
    			if (processes.size() == 0) {
    				System.out.println("No Running Local Processes");
    			} else {
    				/* Print out all local processes (in local processes set) */
    				iterator = processes.iterator();
    				System.out.println("Local Running Processes");
    	    		while (iterator.hasNext()) {
    	                MigratableThread t = iterator.next();
    					System.out.println(t.process.toString());
    				}
    			}
    		} else if (inputString.equals("quit")) {
    			System.out.println("Goodbye!");
    			return;
    		} else {
    			if (inputString.contains("#")) {
    				System.out.println("Error: Class/Arguments cannot contain '#' character");
    			} else {
    				manager.insertToBuffer("#" + inputString);
    			}
    		}
    	}
	}
}