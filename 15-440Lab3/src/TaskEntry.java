import java.io.Serializable;

public class TaskEntry implements Serializable{

	private static final long serialVersionUID = -6972555583224437966L;
	
	/* Container and message class between Master and Participants */
	public int id;
	public String jobName;
	public ConfigurationMRR<?,?,?,?> config;
	
	public String toString() {
		return this.jobName;
	}
}
