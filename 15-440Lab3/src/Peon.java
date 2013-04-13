import java.net.Socket;
import java.util.HashMap;


public class Peon {

	/* Stores information on the current participants */
	public String host;
	public int port;
	public int power;
	public int dead;
	public Socket connection;
	public HashMap<TaskID, TaskEntry> runningTasks;

}
