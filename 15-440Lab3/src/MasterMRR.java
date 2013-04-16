import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashMap;
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
	private int timeout;
	private int availableTasks;
	private int localListenPort;
	private boolean remoteStart;
	
	public MasterMRR() {
		super();
		this.peons = new ConcurrentLinkedQueue<Peon>();
		this.pendingTasks = new ConcurrentLinkedQueue<TaskEntry>();
		this.jobs = new ConcurrentHashMap<Long, JobEntry>();
		/* Turn remote start on by default */
		this.remoteStart = true;
	}

	public void main(String args[]) {
		RandomAccessFile config = null;
		String configLineRead = null;
		String[] configParse = null;
		String configParameter = null;
		String configValue = null;
		int pow;
		Iterator<Peon> peon;
		HashSet<Thread> handlers = new HashSet<Thread>();
		
		/* Check arguments and print usage if incorrect */
		if (args.length != 1) {
			System.out.println("java ServerMRR [configuration file path]");
		}
		
		/* Begin by reading in the config file */
		try {
			config = new RandomAccessFile(args[0], "r");
		} catch (FileNotFoundException excpt) {
			System.out.println(" ServerMRR: Failed to open config file, please check file path and try again");
			System.exit(-1);
		}
		
		/* Read the lines of the file and parse the information */
		try {
			configLineRead = config.readLine();
		} catch (IOException e) {
			System.out.println(" ServerMRR: Failed to read config file");
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
						newPeon.power = 1;
						newPeon.runningTasks = new HashMap<TaskID, TaskEntry>();
						if ((newPeon.port > 65535) || (newPeon.port < 0)) {
							System.out.println(" MasterMRR: Peon " + newPeon.host + "'s port is out of range");
						} else {
							this.peons.add(newPeon);
						}
					}
				} catch (Exception excpt) {
					System.out.println(" ServerMRR: Failed to parse participant list in config file, please check the form");
					System.exit(-4);
				}
			} else if (configParameter.equalsIgnoreCase("username")) {
				this.user = configValue;
			} else if (configParameter.equalsIgnoreCase("listen_port")) {
				this.listenPort = Integer.parseInt(configValue);
			} else if (configParameter.equalsIgnoreCase("retries")) {
				this.retries = Integer.parseInt(configValue);
			} else if (configParameter.equalsIgnoreCase("local_port")) {
				this.localListenPort = Integer.parseInt(configValue);
			} else if (configParameter.equalsIgnoreCase("remote_start")) {
				if (configValue.equalsIgnoreCase("off")) {
					this.remoteStart = false;
				}
			} else if (configParameter.equalsIgnoreCase("timeout")) {
				this.timeout = Integer.parseInt(configValue);
			}
				// TODO: Add more parameters if needed ...

			try {
				configLineRead = config.readLine();
			} catch (IOException e) {
				System.out.println(" ServerMRR: Failed to read config file");
				System.exit(-2);
			}
		}
		
		try {
			config.close();
		} catch (IOException e) {
			/* Failed to close the file, whatever will we do.... */
		}
		
		/* Check ports for errors */
		if ((this.localListenPort > 65535) || (this.localListenPort < 0)) {
			System.out.println(" ServerMRR: Local listen port out of range");
		} else if ((this.listenPort > 65535) || (this.listenPort < 0)) {
			System.out.println(" ServerMRR: Master listen port out of range");
		}
		
		pow = this.peons.size();
		
		/* Create the listener for dead participants */
        Thread PeonListener = new Thread(new PeonListener(this.listenPort, this.peons));
        PeonListener.start();
        
		/* Create and start UI */
		Thread UI = new Thread(new UserInterface(this));
		UI.start();
		
		/* Run Main loop */
		while (UI.isAlive()) {
			
			/* Loop through all the peons and spawn threads to process their status' and give
			 * them new jobs, etc */
			peon = this.peons.iterator();
			this.currentPower = Math.max(pow, this.peons.size());
			System.out.println(" Total Power: " + Integer.toString(this.currentPower));
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
				Thread t = new Thread(new MasterJobHandlerMRR(this, j));
				handlers.add(t);
				t.start();
			}
			
			/* Wait for all the handlers to terminate to prevent overlapping issues */
			for (Thread t : handlers) {
				try {
					t.join();
				} catch (InterruptedException e) {
					/* Should never happen */
					System.out.println(" ServerMRR: Process wait interrupted, exiting...");
					System.exit(-3);
				}
			}
		}
		System.out.println("Leaving...");
		return;
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
	
	public void addJob(JobEntry job) {
		this.jobs.put(job.id, job);
		return;
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
	
	public int getTimeout() {
		return this.timeout;
	}
	
	public String getUsername() {
		return this.user;
	}
	
	public int getListenPort() {
		return this.listenPort;
	}
	
	public int getLocalListenPort() {
		return this.localListenPort;
	}
	
	public int getAvailableTasks() {
		return this.availableTasks;
	}
	
	public boolean stopJob(long jobID, Exception err) {
		/* Terminates a job */
		JobEntry j = this.findJob(jobID);
		if (j != null) {
			j.err = err;
			return true;
		}
		return false;
	} 
	
	public void removeJob(long jobID) {
		this.jobs.remove(jobID);
		return;
	}
	
	public boolean remoteStart() {
		return this.remoteStart;
	}
	
	public int getNodes() {
		int nodes = 0;
		for (Peon p : this.peons) {
			if (p.dead > 0) {
				nodes++;
			}
		}
		return nodes;
	}
	
	public int getTotalNodes() {
		return this.peons.size();
	}
}

