import java.util.HashSet;


public class JobEntry {

	public String name;
	public int id;
	public String host;
	public int port;
	public HashSet<Integer> tasks;
	
	public String toString() {
		return Integer.toString(id) + "  " + name + "  " + host + ":" + Integer.toString(port) + "  " + tasks.toString(); 
	}
}
