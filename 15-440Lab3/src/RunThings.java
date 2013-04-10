import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class RunThings {

	public static void main(String[] args) throws IOException {
		String s = null;
		
		Process p = Runtime.getRuntime().exec(new String[]{"ssh", args[0] + "@" + args[1], "'cd " + System.getProperty("user.dir") + "; java Basic'"});
        
        BufferedReader stdInput = new BufferedReader(new 
             InputStreamReader(p.getInputStream()));

        BufferedReader stdError = new BufferedReader(new 
             InputStreamReader(p.getErrorStream()));

        // read the output from the command
        System.out.println("Here is the standard output of the command:\n");
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }
     // read any errors from the attempted command
        System.out.println("Here is the standard error of the command (if any):\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
	}
}
