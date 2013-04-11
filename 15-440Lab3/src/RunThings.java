import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class RunThings {

	public static void main(String[] args) throws IOException {
		String s = null;
		
		Process p = Runtime.getRuntime().exec("./ssh_work " + args[0] + " " + args[1] + " " + System.getProperty("user.dir"));
	}
}
