import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentLinkedQueue;


public class JobExec implements Runnable {

	private JobMRR master;
	private String jobName;
	private ConfigurationMRR<?, ?, ?> config;
	
	public JobExec(JobMRR master, String jobName, ConfigurationMRR<?,?,?> config) {
		super();
		this.jobName = jobName;
		this.config = config;
	}

	@Override
	public void run() {
		/* Make a new job entry to be sent to the local participant */
		JobEntry submitJob = new JobEntry();
		submitJob.config = this.config;
		submitJob.name = this.jobName;
		try {
			submitJob.host = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			this.master.setException(e);
			return;
		}
		submitJob.port = this.config.port;
		submitJob.err = null;
		submitJob.runningTasks = new ConcurrentLinkedQueue<TaskID>();
		submitJob.completeTasks = new ConcurrentLinkedQueue<TaskID>();
		submitJob.id = Math.abs(submitJob.host.hashCode()) + Math.abs(submitJob.name.hashCode()) + Math.abs((new Long(System.currentTimeMillis()).hashCode()));
		
		// TODO: Ship this job to local participant
		
	}

}
