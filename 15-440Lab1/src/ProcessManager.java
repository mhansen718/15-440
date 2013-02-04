import java.io.*;
import java.net.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class ProcessManager {
    
    private ThreadGroup processes;
    private final String hostname;
    private final int port;
    private String buffer;
    
    public ProcessManager(String hostname, int port) {
		super();
		this.hostname = hostname;
        this.port = port;
        this.processes = new ThreadGroup("Local Running Processes");
	}
    
    // I'll make this two threads, one that manages processes, one that is a slave
    private void masterManager() {
        new Thread(new MasterManager(new MasterListener(port))).start();
        return;
    }
    
    public void insertToBuffer(String input) {
        this.buffer += input;
        return;
    }
    
    public ThreadGroup getProcesses() {
        return processes;
    }
    
    private void slaveManager() {
    	Thread[] processesAsThreads;
    	Thread UI;
        Socket socket = null;
    	PrintWriter out = null;
        BufferedReader in = null;
        String input;
        String[] splitInput;
        
        try {
            socket = new Socket(hostname,port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Could not connect to server.");
            return;
        }
        
    	/* Create the user interface as separate thread */
    	try {
    		UI = new Thread(new userInterface(this));
    		UI.start();
    	} catch (Exception expt) {
    		System.out.println("Error: Failed to create UI; Exiting...");
    		return;
    	}
    	
    	while (UI.getState() != Thread.State.TERMINATED) {
            try {
				input = in.readLine();
			} catch (IOException excpt) {
				System.out.println("Error: Failed to commune with master");
				return;
			}
            
            if (input.equals("PLOP")) {
                out.println(plopProcess() + "\nEND");
            } else if (input.startsWith("PLANT")) {
                plantProcess(input.substring(5));
                out.println("SUCCESS\nEND");
            } else if (input.equals("BEAT")) {
                out.println(processes.activeCount() + "#" + buffer);
                buffer = "";
            } else {
                splitInput = input.split(" ", 2);
                newProcess(splitInput[1].split(" "),splitInput[0]);
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
    	String humanName;
    	ObjectOutputStream objOut;
    	
    	/* Take the plot process and suspend it */
    	try {
    		plopProcess = ((MigratableProcess) processesAsThreads[0]);
    		plopName = processesAsThreads[0].getName();
    		humanName = convertFromThreadName(plopName);
    	} catch (ArrayIndexOutOfBoundsException excpt) {
    		System.out.println("Error: No running processes on this node");
    		return "";
    	}
    	
    	try {
    		plopProcess.suspend();
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to suspend process " + humanName);
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
    		System.out.println("Error: Failed to open file stream for serialization");
    		return "";
    	}
    	
    	try {
    		objOut.writeObject(plopProcess);
    		objOut.flush();
    	} catch (NotSerializableException excpt) {
    		System.out.println("Error: Process " + humanName + " does not appear serializable");
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
    	Object plantProcess;
    	File thisFile;
    	Thread processThread;
    	String threadName;
    	String processArgs;
   
    	/* Get name and class data from file */
    	try {
    		/* Remove extension and make a human readable version for errors */
    		threadName = fileName.substring(0, (fileName.length() - 4));
    		processArgs = ProcessManager.convertFromThreadName(threadName);
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to parse serialized file");
    		return;
    	}
    	
    	/* Open file and read data from it */
    	try {
    		objIn = new ObjectInputStream(new FileInputStream(fileName));
    		thisFile = new File(fileName);
    	} catch (IOException excpt) {
    		System.out.println("Error: IO error in reading file " + fileName);
    		return;
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to open file stream for deserialization");
    		return;
    	}
    	
    	try {
    		plantProcess = objIn.readObject();
    	} catch (ClassNotFoundException excpt) {
    		System.out.println("Error: Class " + processArgs + " was not found");
    		return;
    	} catch (InvalidClassException excpt) {
    		System.out.println("Error: Class " + processArgs + " is not a valid serializable class");
    		return;
    	} catch (IOException excpt) {
    		System.out.println("Error: Failed to read from input stream");
    		return;
    	}
    	
    	/* Run new thread for the class */
    	try {
    		processThread = new Thread(processes, ((Runnable) plantProcess), threadName);
    		processThread.start();
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to run new process of class " + processArgs);
    		return;
    	}

    	/* Close, delete the file and leave if alls well */
    	try {
			objIn.close();
			if (!(thisFile.delete())) {
				System.out.println("Error: Failed to delete serialized object file");
			}
		} catch (IOException excpt) {
			System.out.println("Error: Failed to close output stream successfully");
		}
    	
    	return;
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
    		newProcessArgs = ((Object[]) Arrays.copyOfRange(args, 1, args.length));
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
    		processThread = new Thread(processes, ((Runnable) newProcess), 
    				ProcessManager.convertToThreadName(args, id));
    		processThread.start();
    	} catch (Exception excpt) {
    		System.out.println("Error: Failed to run new process of class " + args[0]);
    		return;
    	}
    	
    	return;
    }
    
    /* Utility functions */
    public static String convertFromThreadName(String threadName) {
    	String[] splitName = threadName.split("#");
    	String humanName = new String();
    	
    	for (int i = 0; i < splitName.length; i++) {
    		humanName.concat(splitName[i] + " ");
    	}
    	
    	return humanName;
    }
    
    private static String convertToThreadName(String[] args, String id) {
    	String threadName = new String();
    	
    	for (int i = 0; i < args.length; i++) {
    		threadName.concat(args[i] + "#");
    	}
    	
    	threadName.concat(id);
    	
    	return threadName;
    }
    
    public int runningProcesses() {
        return processes.activeCount();
    }
    
    private void begin() {
    	/* Run either as master or slave based on hostname */
    	if (hostname == null) {
            masterManager();
        }
        slaveManager();
    	return;
    }
    
	public static void main(String[] args) {
        String hostnameLocal = null;
        int port = 27000; // Default port (I think 27000 is safe, change it if it isn't)
        int i = 0;
        while (i < args.length) {
            if (args[i].equals("-c")) {
                i++;
                hostnameLocal = args[i];
                i++;
            } else if (args[i].equals("-p")) {
                i++;
                try {
                	port = Integer.parseInt(args[i]);
                } catch (NumberFormatException excpt) {
                	System.out.println("Invalid port number: " + args[i]);
                }
                i++;
            } else {
                System.out.println("Invalid argument: " + args[i]);
		return;
            }
        }
        ProcessManager p = new ProcessManager(hostnameLocal, port);
        p.begin();
	}
}
