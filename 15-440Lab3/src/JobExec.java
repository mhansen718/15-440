import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
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
        Socket socket = null;
        ObjectOutputStream out;
    
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
		
		try {
            socket = new Socket(InetAddress.getLocalHost(), this.config.participantPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(submitJob);
        } catch (IOException e) {
            System.err.println("Failed to submit mapReduce job");
        }
		
	}

}
