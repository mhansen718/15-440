import java.io.Serializable;


public class TaskID implements Serializable {

	private static final long serialVersionUID = -6124399538927318553L;
	
	/* Class to id Tasks */
	public long jobID;
	public int start;
	public int end;
	public Exception err;

	@Override
	public boolean equals(Object obj) {
		return ((this.jobID == ((TaskID) obj).jobID) &&
				(this.start == ((TaskID) obj).start));
	}
	
	
	@Override
	public int hashCode() {
		return ((int) this.jobID) + (this.start ^ this.end);
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
	
	public String toString() {
		return Long.toString(this.jobID) + "." + Integer.toString(this.start) + "." + Integer.toString(this.end);
	}

}
