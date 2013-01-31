import java.io.*;
import java.lang.reflect.Constructor;
import java.util.Scanner;

public class ProcessManager {
    
    private ThreadGroup processes;
    private final String hostname; 
    
    public ProcessManager(String hostname) {
		super();
		this.hostname = hostname;
	}
    
    // I'll make this two threads, one that manages processes, one that is a slave
    private void masterManager() {
        
    }
    
    private void slaveManager() {
        //TODO: Connect to master
    	Thread[] processesAsThreads;
    	Thread UI;
    	
    	/* Create the user interface as separate thread */
    	try {
    		UI = new Thread(new userInterface(processes));
    		UI.start();
    	} catch (Exception expt) {
    		System.out.println("Error: Failed to create UI; Exiting...");
    		return;
    	}
    	
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
    
    private void plopProcess() {
    	/* Get the first thread in the processes and suspend it. Then, serialize it. */
    	Thread[] processesAsThreads = new Thread[processes.activeCount()];
    	processes.enumerate(processesAsThreads);
    	MigratableProcess plopProcess;
    	String plopName;
    	ObjectOutputStream objOut;
    	
    	/* Take the plot process and suspend it */
    	try {
    		plopProcess = ((MigratableProcess) processesAsThreads[0]);
    		plopName = processesAsThreads[0].getName();
    	} catch (ArrayIndexOutOfBoundsException excpt) {
    		System.out.println("Error: No running processes on this node");
    		return;
    	}
    	
    	try {
    		plopProcess.suspend();
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to suspend process " + processesAsThreads[0].getName());
    		return;
    	}
    	
    	/* Now serialize it */
    	try {
    		objOut = new ObjectOutputStream(new FileOutputStream(plopName + ".ser"));
    	} catch (IOException excpt) {
    		System.out.println("Error: Failed to open file stream for serialization due to IO Error");
    		return;
    	} catch (SecurityException excpt) {
    		System.out.println("Error: Permission denied in creating output stream");
    		return;
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to open file stream");
    		return;
    	}
    	
    	try {
    		objOut.writeObject(plopProcess);
    		objOut.flush();
    	} catch (NotSerializableException excpt) {
    		System.out.println("Error: Process " + plopName + " does not appear serializable");
    		return;
    	} catch (IOException excpt) {
    		System.out.println("Error: Failed to write object to file");
    		return;
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to open file stream");
    		return;
    	}

    	/* Close and leave if alls well */
    	try {
			objOut.close();
		} catch (IOException excpt) {
			System.out.println("Error: Failed to close output stream successfully");
		}
    	return;
    }
    
    private void planetProcess(String processName) {
    	
    	ObjectInputStream objIn;
    }
    
    private void newProcess(String[] args) {
    	/* Run a new process on this node. The master will give the*/
    	Class objClass;
    	Constructor objConstruct;
    	
    	try {
    		objClass = Class.forName(args[0]);
    	} catch (ClassNotFoundException excpt) {
    		System.out.println("Error: Class: " + processClass + " was not found");
    		return;
    	} catch (Exception expt) {
    		System.out.println("Error: Failed to find class for name: " + processClass);
    		return;
    	}
    	try {
    		objConstruct = objClass.getConstructor();
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to get constructor for class " + processClass);
        	return;
    	}
    	
    }
    
    public int runningProcesses() {
        return processes.activeCount();
    }
    
    private void begin() {
    	/* Run either as master or slave based on hostname */
    	if (hostname == null) {
            masterManager();
        } else {
            slaveManager();
        }
    	return;
    }
    
	public static void main(String[] args) {
        String hostnameLocal = null;
        int i = 0;
        while (i < args.length) {
            if (args[i] == "-c") {
                i++;
                hostnameLocal = args[i];
                i++;
            } else {
                System.out.println("Invalid argument: " + args[i]);
            }
        }
        ProcessManager p = new ProcessManager(hostnameLocal);
        p.begin();
	}
}
