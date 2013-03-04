import java.net.InetAddress;
import java.net.UnknownHostException;


public class TestRMIServer {

	public void main(String[] args) {
		/* A host for the objects TestRMIClient will call on */
		BasicTests basic;
		AdvancedTests adv;
		RMIRegistryClient myRMI = new RMIRegistryClient(args[0], Integer.parseInt(args[1]));
		
		
	}
}
