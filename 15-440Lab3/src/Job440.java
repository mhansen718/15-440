
public class Job440 {

	private Configuration440<?, ?, ?, ?> config;
	private String jobName;
	
	public Job440(Configuration440<?, ?, ?, ?> config) {
		super();
		this.config = config;
	}
	
	public Job440(Configuration440<?, ?, ?, ?> config, String name) {
		super();
		this.config = config;
		this.jobName = name;
	}
	
	public void submit() throws Exception {
		/* Submit this job to the MapReduce Master for execution,
		 * throws exception if rejected by master (for reasons like repeat name or something) */
		
		/* TODO: Connect to master and give the job to him, configuration class name and all */
		return;
	}
	
	public boolean isComplete() throws Exception {
		/* Connect to the master and asks if job is complete */
		
		// TODO Connect to master and ask if the job is done
		return true;
	}
	
	public void waitOnJob() throws Exception {
		/* Sleep until job is done */
		// TODO: Connect to master and set a flag that asks the master to contact this node when job is done
		return;
	}
}
