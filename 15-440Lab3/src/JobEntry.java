import java.util.HashSet;


public class JobEntry {

	public String name;
	public long id;
	public String host;
	public int port;
	public ConfigurationMRR<?,?,?,?> config;
	public HashSet<TaskID> runningTasks;
	public HashSet<TaskID> completeTasks;
	
	public String toString() {
		return Long.toString(id) + "  " + name + "  " + host + ":" + Integer.toString(port) + "  " + runningTasks.toString(); 
	}
}
