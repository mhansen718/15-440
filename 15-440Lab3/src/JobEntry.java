import java.util.HashSet;


public class JobEntry {

	public String name;
	public long id;
	public String host;
	public int port;
	public HashSet<Integer> tasks;
	
	public String toString() {
		return Long.toString(id) + "  " + name + "  " + host + ":" + Integer.toString(port) + "  " + tasks.toString(); 
	}
}
