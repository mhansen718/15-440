import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.AlreadyBoundException;

public class TestRMIServer {

	public static void main(String[] args) {
		/* A host for the objects TestRMIClient will call on */
		BasicTestsImpl basic = new BasicTestsImpl("Hi", 4);
		AdvancedTestsImpl adv = new AdvancedTestsImpl();
		RMIRegistryClient myRMI;
		try {
			myRMI = new RMIRegistryClient(args[0], Integer.parseInt(args[1]));
			
			System.out.println("Made new local RMI, now binding");
			myRMI.bind("basics", adv);
			System.out.println("Bound 1!");
			myRMI.bind("adv", adv);
			System.out.println("Bound 2!");
			myRMI.rebind("basics", basic);
			System.out.println("All bound!");
			
			System.out.println("Listing Bonds:");
			for (String obj : myRMI.list()) {
				System.out.println(obj);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Failed to do RMI: " + e);
			e.printStackTrace();
		}
		
	}
}