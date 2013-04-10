import java.io.Serializable;

public class JobEntry implements Serializable{

	private static final long serialVersionUID = -6972555583224437966L;
	
	/* Container and message class between Master and Participants */
	public String jobName;
	
	public String toString() {
		return this.jobName;
	}
}
