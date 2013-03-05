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
			myRMI.bind("me2", basic);
			System.out.println("Bind for unbinding...");
			myRMI.unbind("me2");
			System.out.println("Unbound!");
			
			try {
				myRMI.bind("adv", basic);
				System.out.println("Somehow, 'adv' was bound again....");
			} catch (AlreadyBoundException excpt) {
				System.out.println("A bind failed on an already bound thing: GOOD " + excpt);
			}
			
			try {
				BasicTests fail = (BasicTests) myRMI.lookup("FAIL");
				System.out.println("Somehow, 'FAIL' was found.....");
			} catch (NotBoundException excpt) {
				System.out.println("A lookup on a non-bound object failed: GOOD " + excpt);
			}
			
			try {
				myRMI.unbind("FAIL");
				System.out.println("Somehow, 'FAIL' was unbound.....");
			} catch (NotBoundException excpt) {
				System.out.println("An unbind on a non-bound object failed: GOOD " + excpt);
			}
			
			System.out.println("Listing Bonds:");
			for (String obj : myRMI.list()) {
				System.out.println(obj);
			}
		} catch (Exception e) {
			System.out.println("Failed to do RMI: " + e);
			e.printStackTrace();
		}
		
	}
}