import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MasterMRR {
	
	private ConcurrentLinkedQueue<Peon> peons;
	private ConcurrentLinkedQueue<TaskEntry> pendingTasks;
	private int currentPower;
	private ConcurrentHashMap<Long, JobEntry> jobs;
	private String user;
	private int listenPort;
	private int retries;
	private int availableTasks;
	
	public MasterMRR() {
		super();
		this.peons = new ConcurrentLinkedQueue<Peon>();
		this.pendingTasks = new ConcurrentLinkedQueue<TaskEntry>();
		this.jobs = new ConcurrentHashMap<Long, JobEntry>();
	}

	public void main(String args[]) {
		RandomAccessFile config = null;
		String configLineRead = null;
		String[] configParse = null;
		String configParameter = null;
		String configValue = null;
		int pow = this.peons.size();
		Iterator<Peon> peon;
		HashSet<Thread> handlers = new HashSet<Thread>();
		
		/* Check arguments and print usage if incorrect */
		if (args.length != 1) {
			System.out.println("java MasterMRR [configuration file path]");
		}
		
		/* Begin by reading in the config file */
		try {
			config = new RandomAccessFile(args[0], "r");
		} catch (FileNotFoundException excpt) {
			System.out.println(" MasterMRR: Failed to open config file, please check file path and try again");
			System.exit(-1);
		}
		
		/* Read the lines of the file and parse the information */
		try {
			configLineRead = config.readLine();
		} catch (IOException e) {
			System.out.println(" MasterMRR: Failed to read config file");
			System.exit(-2);
		}
		
		while (configLineRead != null) {
			/* Parse the line and case over the parameters that should be in the config file */
			configParse = configLineRead.split("=");
			configParameter = configParse[0];
			configValue = configParse[1];
			if (configParameter.equalsIgnoreCase("participants")) {
				/* Loop through and get all the participants */
				try {
					for (String part : configValue.split(", ")) {
						Peon newPeon = new Peon();
						newPeon.host = part.split(":")[0];
						newPeon.port = Integer.parseInt(part.split(":")[1]);
						newPeon.dead = 0;
						this.peons.add(newPeon);
					}
				} catch (Exception excpt) {
					System.out.println(" MasterMRR: Failed to parse participant list in config file, please check the form");
					System.exit(-4);
				}
			} else if (configParameter.equalsIgnoreCase("username")) {
				this.user = configValue;
			} else if (configParameter.equalsIgnoreCase("listen_port")) {
				this.listenPort = Integer.parseInt(configValue);
			} else if (configParameter.equalsIgnoreCase("retries")) {
				this.retries = Integer.parseInt(configValue);
			}
				// TODO: Add more parameters if needed ...

			try {
				configLineRead = config.readLine();
			} catch (IOException e) {
				System.out.println(" MasterMRR: Failed to read config file");
				System.exit(-2);
			}
		}
		try {
			config.close();
		} catch (IOException e) {
			/* Failed to close the file, whatever will we do.... */
		}
		
		/* Create and start UI */
		Thread UI = new Thread(new UserInterface(this));
		UI.start();
		
		/* Run Main loop */
		while (UI.isAlive()) {
			
			/* Loop through all the peons and spawn threads to process their status' and give
			 * them new jobs, etc */
			peon = this.peons.iterator();
			this.currentPower = pow;
			pow = 0;
			this.availableTasks = this.pendingTasks.size();
			while (peon.hasNext()) {
				Peon p = peon.next();
				
				/* Add its power to the next status of power */
				if (p.dead > 0) {
					pow += p.power;
				}
				
				Thread t = new Thread(new MasterPeonHandlerMRR(this, p));
				handlers.add(t);
				t.start();
			}
			
			for (JobEntry j : this.jobs.values()) {
				// TODO: Spawn ahndlers to process the jobs
			}
			
			/* Wait for all the handlers to terminate to prevent overlapping issues */
			for (Thread t : handlers) {
				try {
					t.join();
				} catch (InterruptedException e) {
					/* Should never happen */
					System.out.println(" MasterMRR: Process wait interrupted, exiting...");
					System.exit(-3);
				}
			}
		}
	}
	
	public void addTask(TaskEntry task) {
		/* Give out the jobs list */
		this.pendingTasks.add(task);
		return;
	}
	
	public Collection<JobEntry> getJobs() {
		/* Return the whole job list */
		return this.jobs.values();
	}
	
	public JobEntry findJob(long id) {
		return this.jobs.get(id);
	}
	
	public TaskEntry getTask() {
		return this.pendingTasks.poll();
	}
	
	public int getCurrentPower() {
		return this.currentPower;
	}
	
	public int getRetries() {
		return this.retries;
	}
	
	public String getUsername() {
		return this.user;
	}
	
	public int getListenPort() {
		return this.listenPort;
	}
	
	public int getAvailableTasks() {
		return this.availableTasks;
	}
}

