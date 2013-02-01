import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
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
    
    private String plopProcess() {
    	/* Get the first thread in the processes and suspend it. Then, serialize it.
    	 * Output the serialized filename */
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
    		return "";
    	}
    	
    	try {
    		plopProcess.suspend();
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to suspend process " + processesAsThreads[0].getName());
    		return "";
    	}
    	
    	/* Now serialize it */
    	try {
    		objOut = new ObjectOutputStream(new FileOutputStream(plopName + ".ser"));
    	} catch (IOException excpt) {
    		System.out.println("Error: Failed to open file stream for serialization due to IO Error");
    		return "";
    	} catch (SecurityException excpt) {
    		System.out.println("Error: Permission denied in creating output stream");
    		return "";
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to open file stream");
    		return "";
    	}
    	
    	try {
    		objOut.writeObject(plopProcess);
    		objOut.flush();
    	} catch (NotSerializableException excpt) {
    		System.out.println("Error: Process " + plopName + " does not appear serializable");
    		return "";
    	} catch (IOException excpt) {
    		System.out.println("Error: Failed to write object to file");
    		return "";
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to open file stream");
    		return "";
    	}

    	/* Close and leave if alls well */
    	try {
			objOut.close();
		} catch (IOException excpt) {
			System.out.println("Error: Failed to close output stream successfully");
		}
    	return plopName + ".ser";
    }
    
    private void plantProcess(String fileName) {
    	/* Begin a process that was formerly serialized */
    	ObjectInputStream objIn;
    	File here = new File(".");
    	String[] potentials = here.list();
    	String[] parseFileName;
    	String className;
    	String id;
   
    	try {
    		parseFileName = fileName.split(".");
    		className = parseFileName[0];
    		id = parseFileName[1];
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to parse serialized file");
    		return;
    	}

    }
    
    private void newProcess(String[] args, String id) {
    	/* Run a new process on this node. The master will give the args and an id number*/
    	Class<?> objClass;
    	Constructor<?> objConstructs;
    	Object newProcess;
    	Object[] newProcessArgs = new Object[1];
    	Thread processThread;
    	
    	try {
    		objClass = Class.forName(args[0]);
    	} catch (ClassNotFoundException excpt) {
    		System.out.println("Error: Class " + args[0] + " was not found");
    		return;
    	} catch (Exception expt) {
    		System.out.println("Error: Failed to ressolve class for name " + args[0]);
    		return;
    	}
    	
    	try {
    		objConstructs = objClass.getConstructor();
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to get constructor for class " + args[0]);
        	return;
    	}
    	
    	try {
    		newProcessArgs[0] = ((Object) Arrays.copyOfRange(args, 1, args.length));
    		newProcess = objConstructs.newInstance(newProcessArgs);
    		if (newProcess instanceof MigratableProcess) {
    			System.out.println("Error: Class " + args[0] + " is not a MigratableProcess");
    			return;
    		}
    	} catch (IllegalArgumentException excpt) {
    		System.out.println("Error: Illegal argument provided to class " + args[0]);
    		return;
    	} catch (InvocationTargetException excpt) {
    		System.out.println("Error: Constructor for class " + args[0] + " threw exception " + excpt);
    		return;
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to create new instance of class " + args[0]);
    		return;
    	}
    	
    	try {
    		processThread = new Thread(processes, ((Runnable) newProcess), args[0] + id);
    		processThread.start();
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to run new process of class " + args[0]);
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
