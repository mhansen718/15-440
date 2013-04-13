import java.io.Serializable;
import java.util.HashSet;

public class TaskEntry implements Serializable{

	private static final long serialVersionUID = -6972555583224437966L;
	
	/* Container and message class between Master and Participants */
	public TaskID id;
	public String file1;
    public String file2;
    public int recordSize;
	public ConfigurationMRR<?,?,?> config;
	
}
