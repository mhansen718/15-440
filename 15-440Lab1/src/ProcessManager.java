import java.io.*;
import java.net.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

public class ProcessManager {
    
    private Set<MigratableThread> processes;
    private String hostname;
    private final int port;
    private volatile String buffer;
    private volatile String input;
    private BufferedReader in;
    
    public ProcessManager(String hostname, int port) {
		super();
		this.hostname = hostname;
        this.port = port;
        this.input = "";
        this.in = null;
        this.buffer = "";
        this.processes = new HashSet<MigratableThread>();
	}
    
    // I'll make this two threads, one that manages processes, one that is a slave
    private void masterManager() {
        ReentrantLock lock = new ReentrantLock();
        new Thread(new MasterManager(new MasterListener(port, lock), lock)).start();
        return;
    }
    
    public void insertToBuffer(String input) {
        this.buffer += input;
        return;
    }
    
    public Set<MigratableThread> getProcesses() {
        return processes;
    }
    
    public BufferedReader getIn() {
    	return this.in;
    }
    
    public boolean inputSafe() {
    	return input.equals("");
    }
    
    public void writeInput(String newInput) {
    	this.input = newInput;
    	return;
    }
    
    private void slaveManager() {
    	Thread UI;
    	Thread slaveListen;
        Socket socket = null;
    	PrintWriter out = null;
        String[] splitInput;
        Iterator<MigratableThread> iterator;
        
        try {
            socket = new Socket(hostname, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Could not connect to server; Exiting...");
            return;
        }
        
        /* Create a listener to get instructions from the master */
    	try {
    		slaveListen = new Thread(new SlaveListener(this));
    		slaveListen.start();
    	} catch (Exception expt) {
    		System.out.println("Error: Failed to create listen thread; Exiting...");
    		return;
    	}
    	
    	System.out.println("System Initiating // Master@" + hostname + ":" + port);
    	/* Create the user interface as separate thread */
    	try {
    		UI = new Thread(new userInterface(this));
    		UI.start();
    	} catch (Exception expt) {
    		System.out.println("Error: Failed to create UI; Exiting...");
    		return;
    	}
    	
    	while ((UI.getState() != Thread.State.TERMINATED) && 
    			(slaveListen.getState() != Thread.State.TERMINATED)) {
    		
            if (input.equals("PLOP")) {
            	System.out.println("I am plotting");
                out.println(plopProcess() + "\nEND");
                input = "";
            } else if (input.startsWith("PLANT")) {
            	System.out.println("I am planting");
                plantProcess(input.substring(5));
                out.println("SUCCESS\nEND");
                input = "";
            } else if (input.equals("BEAT")) {
            	if (buffer.length() > 0) {
            		out.println(processes.size() + buffer + "\nEND");
            	} else {
            		out.println(processes.size() + "\nEND");
            	}
            	buffer = "";
            	input = "";
            } else if (input.startsWith("NEW")) {
            	System.out.println("I am newing");
                splitInput = input.split(" ", 3);
                newProcess(splitInput[2].split(" "),splitInput[1]);
                out.println("SUCCESS\nEND");
                input = "";
            }
    		
            iterator = this.processes.iterator();
            
    		while (iterator.hasNext()) {
                MigratableThread t = iterator.next();
    			if (t.processThread.getState() == Thread.State.TERMINATED) {
    				System.out.println();
    				System.out.println("Process " + t.process.toString() + "has terminated.");
    				System.out.print("->> ");
    				iterator.remove();
    			}
    		}
    	}

    	return;
    }
    
    private String plopProcess() {
    	/* Get the first thread in the processes and suspend it. Then, serialize it.
    	 * Output the serialized filename */
    	MigratableThread[] processesAsThreads = processes.toArray(new MigratableThread[0]);
    	MigratableThread plopProcess;
    	String plopName;
    	String humanName;
    	ObjectOutputStream objOut;
    	
    	/* Take the plot process and suspend it */
    	try {
    		plopProcess = processesAsThreads[0];
    		humanName = plopProcess.process.toString();
    		plopName = convertToFileName(humanName.split(" "), plopProcess.id);
    	} catch (ArrayIndexOutOfBoundsException excpt) {
    		System.out.println();
    		System.out.println("Error: No running processes on this node");
    		System.out.print("->> ");
    		return "";
    	}
    	
    	try {
    		plopProcess.process.suspend();
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to suspend process " + humanName);
    		System.out.print("->> ");
    		return "";
    	}
    	
    	/* Now serialize it */
    	try {
    		objOut = new ObjectOutputStream(new FileOutputStream(plopName + ".ser"));
    	} catch (IOException excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to open file stream for serialization due to IO Error");
    		System.out.print("->> ");
    		return "";
    	} catch (SecurityException excpt) {
    		System.out.println();
    		System.out.println("Error: Permission denied in creating output stream");
    		System.out.print("->> ");
    		return "";
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to open file stream for serialization");
    		System.out.print("->> ");
    		return "";
    	}
    	
    	try {
    		objOut.writeObject(plopProcess.process);
    		objOut.flush();
    	} catch (NotSerializableException excpt) {
    		System.out.println();
    		System.out.println("Error: Process " + humanName + " does not appear serializable");
    		System.out.print("->> ");
    		return "";
    	} catch (IOException excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to write object to file");
    		System.out.print("->> ");
    		return "";
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to open file stream");
    		System.out.print("->> ");
    		return "";
    	}

    	/* Close and leave if alls well */
    	try {
			objOut.close();
		} catch (IOException excpt) {
			System.out.println();
			System.out.println("Error: Failed to close output stream successfully");
			System.out.print("->> ");
		}
    	
    	return plopName + ".ser";
    }
    
    private void plantProcess(String fileName) {
    	/* Begin a process that was formerly serialized */
    	ObjectInputStream objIn;
    	Object plantProcess;
    	File thisFile;
    	Thread processThread;
    	String id;
    	String[] parseHelp;
    	MigratableThread newEntry = new MigratableThread();
    	
    	/* Get ID from file name */
    	try {
    		parseHelp = fileName.split("#");
    		id = parseHelp[parseHelp.length - 1].substring(0, (fileName.length() - 5)); 
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to parse file name " + fileName);
    		System.out.print("->> ");
    		return;
    	}
    	/* Open file and read data from it */
    	try {
    		objIn = new ObjectInputStream(new FileInputStream(fileName));
    		thisFile = new File(fileName);
    	} catch (IOException excpt) {
    		System.out.println();
    		System.out.println("Error: IO error in reading file " + fileName);
    		System.out.print("->> ");
    		return;
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to open file stream for deserialization");
    		System.out.print("->> ");
    		return;
    	}
    	
    	try {
    		plantProcess = objIn.readObject();
    	} catch (ClassNotFoundException excpt) {
    		System.out.println("Error: Class  from file " + fileName + " was not found");
    		System.out.print("->> ");
    		return;
    	} catch (InvalidClassException excpt) {
    		System.out.println();
    		System.out.println("Error: Class from file " + fileName + " is not a valid serializable class");
    		System.out.print("->> ");
    		return;
    	} catch (IOException excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to read from input stream");
    		System.out.print("->> ");
    		return;
    	}
    	
    	/* Run new thread for the class */
    	try {
    		processThread = new Thread(((Runnable) plantProcess));
    		processThread.start();
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to run new process");
    		System.out.print("->> ");
    		return;
    	}
    	
    	try {
    		newEntry.process = ((MigratableProcess) plantProcess);
    		newEntry.processThread = processThread;
    		newEntry.id = id;
    		processes.add(newEntry);
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to add process to process list");
    		System.out.print("->> ");
    		return;
    	}

    	/* Close, delete the file and leave if alls well */
    	try {
			objIn.close();
			if (!(thisFile.delete())) {
				System.out.println();
				System.out.println("Error: Failed to delete serialized object file");
				System.out.print("->> ");
			}
		} catch (IOException excpt) {
			System.out.println();
			System.out.println("Error: Failed to close output stream successfully");
			System.out.print("->> ");
		}
    	
    	return;
    }
    
    private void newProcess(String[] args, String id) {
    	/* Run a new process on this node. The master will give the args and an id number*/
    	Class<?> objClass;
    	Constructor<?> objConstructs;
    	Class[] argsClass = new Class[1];
    	Object newProcess;
    	Object[] newProcessArgs = new Object[1];
    	Thread processThread;
    	MigratableThread newEntry = new MigratableThread();
    	
    	try {
    		objClass = Class.forName(args[0]);
    	} catch (ClassNotFoundException excpt) {
    		System.out.println();
    		System.out.println("Error: Class " + args[0] + " was not found");
    		System.out.print("->> ");
    		return;
    	} catch (Exception expt) {
    		System.out.println();
    		System.out.println("Error: Failed to ressolve class for name " + args[0]);
    		System.out.print("->> ");
    		return;
    	}
    	
    	try {
    		argsClass[0] = String[].class;
    		objConstructs = objClass.getConstructor(argsClass);
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to get constructor for class " + args[0] + excpt);
    		System.out.print("->> ");
        	return;
    	}
    	
    	try {
    		newProcessArgs[0] = Arrays.copyOfRange(args, 1, args.length);
    		newProcess = objConstructs.newInstance(newProcessArgs);
    		if (!(newProcess instanceof MigratableProcess)) {
    			System.out.println();
    			System.out.println("Error: Class " + args[0] + " is not a MigratableProcess");
    			System.out.print("->> ");
    			return;
    		}
    	} catch (IllegalArgumentException excpt) {
    		System.out.println();
    		System.out.println("Error: Illegal argument provided to class " + args[0]);
    		System.out.print("->> ");
    		return;
    	} catch (InvocationTargetException excpt) {
    		System.out.println();
    		System.out.println("Error: Constructor for class " + args[0] + " threw exception " + excpt);
    		System.out.print("->> ");
    		return;
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to create new instance of class " + args[0]);
    		System.out.print("->> ");
    		return;
    	}
    	
    	try {
    		processThread = new Thread(((Runnable) newProcess));
    		processThread.start();
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to run new process of class " + args[0]);
    		System.out.print("->> ");
    		return;
    	}
    	
    	try {
    		newEntry.process = ((MigratableProcess) newProcess);
    		newEntry.processThread = processThread;
    		newEntry.id = id;
    		processes.add(newEntry);
    	} catch (Exception excpt) {
    		System.out.println();
    		System.out.println("Error: Failed to add process to process list");
    		System.out.print("->> ");
    		return;
    	}
    	
    	return;
    }
    
    /* Utility functions */
    private static String convertToFileName(String[] args, String id) {
    	String fileName = new String();
    	
    	for (int i = 0; i < args.length; i++) {
    		fileName.concat(args[i] + "#");
    	}
    	
    	fileName.concat(id);
    	
    	return fileName;
    }
    
    public int runningProcesses() {
        return processes.size();
    }
    
    private void begin() {
    	/* Run either as master or slave based on hostname */
    	if (hostname == null) {
            masterManager();
            try {
    			hostname = InetAddress.getLocalHost().getHostName();
    		} catch (UnknownHostException e) {
    			System.out.println("Error: localhost not found; Exiting...");
    			return;
    		}
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
        
        System.exit(0);
	}
}
