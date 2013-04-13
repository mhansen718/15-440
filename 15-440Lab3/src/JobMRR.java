
public class JobMRR {

	private ConfigurationMRR<?, ?, ?> config;
	private String jobName;
	private Thread t;
	private Exception err;
	
	public JobMRR(ConfigurationMRR<?, ?, ?> config) {
		super();
		this.config = config;
		this.jobName = "Job on " + config.inFile;
		this.err = null;
	}
	
	public JobMRR(ConfigurationMRR<?, ?, ?> config, String name) {
		super();
		this.config = config;
		this.jobName = name;
		this.err = null;
	}
	
	public void submit() {
		/* Submit this job to the MapReduce Master for execution,
		 * throws exception if rejected by master (for reasons like repeat name or something) */
		this.t = new Thread(new JobExec(this));
		this.t.start();
		return;
	}
	
	public boolean isComplete() {
		/* Check if we are still waiting on the job */
		return !(this.t.isAlive());
	}
	
	public boolean encounteredException() {
		/* Checks if there was an error during the course of the job */
		return (this.err == null);
	}
	
	public Exception getException() {
		/* Returns the exception if one happened */
		return this.err;
	}
	
	public void waitOnJob() throws Exception {
		/* Sleep until job is done */
		this.t.join();
		return;
	}
	
	public void waitOnJob(long timeout) throws Exception {
		/* Sleep until job is done; now with timeout! */
		this.t.join(timeout);
		return;
	}
	
	/* These methods are for the thread to use to set the exception and filename */
	public void setException(Exception excpt) {
		this.err = excpt;
		return;
	}
	
	public void setFile(String name) {
		this.file = name;
		return;
	}
}
