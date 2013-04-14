import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
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
        ObjectInputStream in;
        JobEntry response = null;
        ServerSocket waitSocket = null;
    
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
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to submit mapReduce job");
            return;
        }
		
        // Now wait for the master to say the job is done
        try {
            waitSocket = new ServerSocket(this.config.port);
            socket = waitSocket.accept();
            in = new ObjectInputStream(socket.getInputStream());
            waitSocket.close();
        } catch (IOException e) {
            System.err.println("Failed to listen for job completion");
            return;
        }
        
        try {
            response = (JobEntry) in.readObject();
        } catch (Exception e) {
            System.err.println("Couldn't understand server message");
            return;
        }
        
        this.master.setException(response.err);
        
        try {
            in.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Failed to close sockets");
        }
        
        return;
	}

}
