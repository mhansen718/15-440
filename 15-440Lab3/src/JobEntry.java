import java.util.HashSet;


public class JobEntry {

	public String name;
	public String host;
	public int port;
	public HashSet<Integer> tasks;
	
	public String toString() {
		return name + "  " + host + ":" + Integer.toString(port) + "  " + tasks.toString(); 
	}
}
