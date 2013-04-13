
public class JobExec implements Runnable {

	private String jobName;
	private ConfigurationMRR<?, ?, ?> config;
	
	public JobExec(String jobName, ConfigurationMRR<?,?,?> config) {
		super();
		this.jobName = jobName;
		this.config = config;
	}

	@Override
	public void run() {
		// TODO: Make this read config, prep a job and give it to the local participant for execution
		
	}

}
