import java.io.Serializable;
import java.util.HashSet;


public class ParticipantStatus implements Serializable {

	private static final long serialVersionUID = -4166192444763637179L;

	public HashSet<TaskID> completedTasks;
	public HashSet<JobEntry> newJobs;
    public HashMap<TaskID, TaskEntry> newTasks;
	public int power;
}
