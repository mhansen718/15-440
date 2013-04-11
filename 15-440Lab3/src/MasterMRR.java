import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.ConcurrentLinkedQueue;


public class MasterMRR {
	
	private ConcurrentLinkedQueue<Peon> peons;
	private ConcurrentLinkedQueue<JobEntry> jobs;

	public void main(String args[]) {
		RandomAccessFile config = null;
		String configLineRead = null;
		String[] configParse = null;
		String configParameter = null;
		String configValue = null;
		
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
			configParameter = configParse[0].toLowerCase();
			configValue = configParse[1];
			switch (configParameter) {
			case "participants":
				try {
					for (String part : configValue.split(", ")) {
						Peon newPeon = new Peon();
						newPeon.host = part.split(":")[0];
						newPeon.port = Integer.parseInt(part.split(":")[1]);
					}
				} catch (Exception excpt) {
					System.out.println(" MasterMRR: Failed to parse participant list in config file, please check the form");
					System.exit(-4);
				}
				// TODO: Add more parameters if needed ...

			}

			try {
				configLineRead = config.readLine();
			} catch (IOException e) {
				System.out.println(" MasterMRR: Failed to read config file");
				System.exit(-2);
			}
		}
		
		/* Create and start UI */
		Thread UI = new Thread(new UserInterface(this));
		UI.start();
		
		/* Run Main loop */
		while (UI.isAlive()) {
			// TODO connect, listen dispatch jobs and stuff :(
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
