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
			// TODO Make this parse the stuff we need
			
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
		
		// TODO connect, listen dispatch jobs and stuff :(
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
