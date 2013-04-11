import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MasterMRR {
	
	private ConcurrentLinkedQueue<Peon> peons;
	private ConcurrentLinkedQueue<JobEntry> jobs;
	private String user;
	private int listenPort;
	
	public MasterMRR() {
		super();
		this.peons = new ConcurrentLinkedQueue<Peon>();
		this.jobs = new ConcurrentLinkedQueue<JobEntry>();
	}

	public void main(String args[]) {
		RandomAccessFile config = null;
		String configLineRead = null;
		String[] configParse = null;
		String configParameter = null;
		String configValue = null;
		Iterator<Peon> peon;
		InetAddress participantAddress = null;
		
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
						newPeon.isDead = true;
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
			peon = this.peons.iterator();
			while (peon.hasNext()) {
				Peon p = peon.next();
				if (p.isDead) {
					/* If the peon is dead, see if it is reachable now and try
					 * to restart the process if needed */
					try {
						participantAddress = InetAddress.getByName(p.host);
					} catch (UnknownHostException excpt) {
						/* No luck reaching the host, report the error but not much we can do */
						System.out.println(" MasterMRR: Warning: host " + p.host + " could not be ressolved");
						continue;
					}
					try {
						if (participantAddress.isReachable(1000)) {
							System.out.println("Remote Starting on " + p.host);
							p.process = Runtime.getRuntime().exec("./ssh_work " + this.user + " " + p.host + " " + 
									System.getProperty("user.dir") + " " + InetAddress.getLocalHost().getHostName() + " " + Integer.toString(this.listenPort));
							p.isDead = false;
						}
					} catch (IOException excpt) {
						/* Had a problem doing the reachability test */
						System.out.println(" MasterMRR: Warning: Failed to reconnect to participant " + p.host);
						continue;
					}
				} else {
					/* Ask the participant how its doing, and 
					 * declare it dead if unreachable */
				}
			}
			// TODO connect, listen dispatch jobs and stuff :(
		}
		
		/* We're quiting now, kill all the participants and do any closing stuff */
		peon = this.peons.iterator();
		while (peon.hasNext()) {
			// TODO: signals all peons to kill themselves
		}
	}
	
	public ConcurrentLinkedQueue<Peon> getPeons() {
		/* Give out the partitipant list */
		return this.peons;
	}
	
	public ConcurrentLinkedQueue<JobEntry> getJobs() {
		/* Give out the jobs list */
		return this.jobs;
	}
}
