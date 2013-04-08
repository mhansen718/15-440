import java.io.IOException;


public class RunThings {

	public static void main(String[] args) throws IOException {
		System.out.println(System.getProperty("user.dir") + " " + args[1]);
		Runtime.getRuntime().exec("ssh " + args[0] + "@" + args[1] + " 'mkdir ~/private/15440/lab3/hi'");/* java -cp " + System.getProperty("user.dir") + " Basic && exit'"); */
		while (true);
	}
}
