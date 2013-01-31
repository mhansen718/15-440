import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProcessManager {
    
    private ThreadGroup processes;
    private final String hostname;
    private volatile boolean quit; 
    
    
    public ProcessManager(String hostname) {
		super();
		this.hostname = hostname;
		this.quit = false;
		
		if (hostname == null) {
            masterManager();
        } else {
            slaveManager();
        }
	}
    
    // I'll make this two threads, one that manages processes, one that is a slave
    private void masterManager() {
        
    }
    
    private void slaveManager() {
        //TODO: Connect to master
    	Thread[] processesAsThreads;
    	
    	Thread UI = new Thread();
    	
    	while (!quit) {
    		// TODO: If heartbeat, plant or plop
    		if (YOUR STUFF) {
    			
    		}
    		
    		processesAsThreads = new Thread[processes.activeCount()];
    		processes.enumerate(processesAsThreads);
    		for (Thread t : processesAsThreads) {
    			if (t.getState() == Thread.State.TERMINATED) {
    				System.out.println("Process " + t.getName() + " has terminated.");
    			}
    		}
    	}
    	
    	return;
    }
    
    private void userInterface() {
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
    			quit = true;
    		}
    		else {
    			//TODO: Send to Master for processing
    		}
    	}
    	
    }
    
    public int runningProcesses() {
        return processes.activeCount();
    }
    
	public static void main(String[] args) {
        String h = null;
        int i = 0;
        while (i < args.length) {
            if (args[i] == "-c") {
                i++;
                h = args[i];
                i++;
            } else {
                System.out.println("Invalid argument!" + args[i]);
            }
        }
        ProcessManager p = new ProcessManager(h);
	}
}
