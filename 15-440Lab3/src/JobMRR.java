import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.TreeMap;


public class JobMRR<MAPIN,REDKEY,REDVAL> {

	private ConfigurationMRR<MAPIN, REDKEY, REDVAL> config;
	private String jobName;
	private Thread t;
	private Exception err;
	
	public JobMRR(ConfigurationMRR<MAPIN, REDKEY, REDVAL> config) {
		super();
		this.config = config;
		this.jobName = "Job on " + config.inFile;
		this.err = null;
	}
	
	public JobMRR(ConfigurationMRR<MAPIN, REDKEY, REDVAL> config, String name) {
		super();
		this.config = config;
		this.jobName = name;
		this.err = null;
	}
	
	public void submit() {
		/* Submit this job to the MapReduce Master for execution,
		 * throws exception if rejected by master (for reasons like repeat name or something) */
        if (this.config.start >= this.config.end) {
            System.err.println("Start record must be less than end record");
            return;
        }
		this.t = new Thread(new JobExec(this, this.jobName, this.config));
		this.t.start();
		return;
	}
	
	public boolean isComplete() {
		/* Check if we are still waiting on the job */
		return !(this.t.isAlive());
	}
	
	public boolean encounteredException() {
		/* Checks if there was an error during the course of the job */
		return !(this.err == null);
	}
	
	public Exception getException() {
		/* Returns the exception if one happened */
		return this.err;
	}
	
	public void waitOnJob() throws InterruptedException {
		/* Sleep until job is done */
		this.t.join();
		return;
	}
	
	public void waitOnJob(long timeout) throws InterruptedException {
		/* Sleep until job is done; now with timeout! */
		this.t.join(timeout);
		return;
	}
	
	public TreeMap<REDKEY, REDVAL> readFile() throws IOException, ClassNotFoundException {
		ObjectInputStream obj = new ObjectInputStream(new FileInputStream(this.config.outFile));
		return ((TreeMap<REDKEY, REDVAL>) obj.readObject());
	}
	
	/* These methods are for the thread to use to set the exception */
	public void setException(Exception excpt) {
		this.err = excpt;
		return;
	}
}
