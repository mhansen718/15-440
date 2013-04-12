import java.io.Serializable;
import java.util.HashSet;


public class ParticipantStatus implements Serializable {

	private static final long serialVersionUID = -4166192444763637179L;

	public HashSet<Integer> completedTasks;
	public int power;
}
