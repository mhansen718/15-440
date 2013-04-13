import java.util.concurrent.ConcurrentLinkedQueue;


public class JobEntry {

	public String name;
	public long id;
	public String host;
	public int port;
	public ConfigurationMRR<?,?,?> config;
	public ConcurrentLinkedQueue<TaskID> runningTasks;
	public ConcurrentLinkedQueue<TaskID> completeTasks;
	public Exception err;
	
	public String toString() {
		return Long.toString(id) + "  " + name + "  " + host + ":" + Integer.toString(port) + "  " + runningTasks.toString(); 
	}
}
