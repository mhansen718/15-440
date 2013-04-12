import java.io.Serializable;


public class TaskID implements Serializable {

	private static final long serialVersionUID = -6124399538927318553L;
	
	/* Class to id Tasks */
	public long jobID;
	public int start;
	public int end;
	
	public boolean equals(TaskID other) {
		return ((this.jobID == other.jobID) &&
				(this.start == other.jobID));
	}

}
