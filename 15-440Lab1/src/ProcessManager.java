import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProcessManager {
    
    private ThreadGroup processes;
    private final String hostname; 
    
    
    public ProcessManager(String hostname) {
		super();
		this.hostname = hostname;

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
    	
    	/* Create the user interface as separate thread */
    	Thread UI = new Thread(new userInterface(processes));
    	UI.start();
    	
    	while (UI.getState() != Thread.State.TERMINATED) {
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
    
    private void plopProcess(String processName) {
    	//TODO: Make it so this suspends and serialized a thread
    }
    
    private void plantProcess(String processName) {
    	//TODO: Make it so this deserialized and runs a process
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
