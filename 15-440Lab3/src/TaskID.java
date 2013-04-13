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
	
	public boolean isAdjacent(TaskID other) {
		return ((this.end == other.start) || 
				(this.start == other.end));
	}
	
	public static TaskID merge(TaskID task1, TaskID task2) {
		TaskID returnTask = new TaskID();
		returnTask.jobID = task1.jobID;
		if (task1.start < task2.start) {
			returnTask.start = task1.start;
			returnTask.end = task2.end;
		} else {
			returnTask.start = task2.start;
			returnTask.end = task1.end;
		}
		return returnTask;
	}
	
	public String toFileName() {
		return Long.toString(this.jobID) + "." + Integer.toString(this.start) + "." + Integer.toString(this.end) + ".mrr";
	}

}
