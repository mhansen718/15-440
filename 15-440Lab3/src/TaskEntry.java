import java.io.Serializable;
import java.util.HashSet;

public class TaskEntry implements Serializable{

	private static final long serialVersionUID = -6972555583224437966L;
	
	/* Container and message class between Master and Participants */
	public int id;
	public String jobName;
	public String host;
	public int port;
	public ConfigurationMRR<?,?,?,?> config;
	public HashSet<Integer> outstandingPrereqs;
	public HashSet<Integer> postreqs;
	
}
