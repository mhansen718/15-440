import java.rmi.RemoteException;
import java.rmi.NotBoundException;
import java.rmi.AlreadyBoundException;

public class TestRMIClient {

	public static void main(String[] args) {
		/* A simple test of various RMI features */
		RMIRegistryClient myRMI;
		try {
			myRMI = new RMIRegistryClient(args[0], Integer.parseInt(args[1]));

			BasicTests basic;
			try {
				basic = (BasicTests) myRMI.lookup("basics");
			} catch (Exception e) {
				System.out.println("Failed to lookup the object! " + e);
				e.printStackTrace();
				return;
			}

			/* Do some basic RMI stuff and see how well it works */
			System.out.println("Im doing RMI!");
			System.out.println("  Basic Stuff: ");
			System.out.println("   remote phrase: " + basic.getPhrase());
			System.out.println("   remote number: " + basic.getNumber());
			basic.addToNumber(12);
			System.out.println("   added 12; now its: " + basic.getNumber());
			try {
				basic.div(0);
				System.out.println("   We divided by 0, cool!!! (But really bad...)");
			} catch (Exception excpt) {
				System.out.println("   We tried div by 0 and got an exception: " + excpt);
			}

			/* Now do a lot of remote object refs using the advanced tests methods */
			System.out.println("  On to the advances stuff:");

			AdvancedTests adv;
			BasicTests newTest;
			BasicTestsImpl localTest = new BasicTestsImpl("Mine", 22);
			Shipper ship = null;
			Shipped cargo = null;
			try {
				adv = (AdvancedTests) myRMI.lookup("adv");
			} catch (Exception e) {
				System.out.println("Failed to lookup the object! " + e);
				return;
			}

			/* Pass-by-Ref */
			newTest = adv.makeNewTests("Hello, Advanced!", 42);
			System.out.println("   Remotely created this object:");
			System.out.println(newTest.toString());
			adv.changeNumber(newTest, 10);
			System.out.println("   Remotely change our reference to a remote object (42->10):");
			System.out.println(newTest.toString());
			adv.changeNumber(localTest, 55);
			System.out.println("   Remotely change a local object (22->55):");
			System.out.println(localTest.toString());

			/* Pass-by-Value */
			ship = adv.getShipper();
			String[] newCargo = {"Corn", "Silver", "Pigs"};
			adv.modifyShipper(newCargo);
			System.out.println("   Local:");
			System.out.println(ship.toString());
			System.out.println("   Remote:");
			System.out.println(adv.getShipper().toString());

			/* Try a bad return value */
			try {
				cargo = adv.getGoods();
				System.out.println("   We serialized it..... so how.... :(");
			} catch (Exception excpt) {
				System.out.println("   Good, we couldnt do it");
			}

			/* Try a bad argument */
			try {
				adv.giveGoods(cargo);
				System.out.println("   Howd we send that?");
			} catch (Exception excpt) {
				System.out.println("   It failed, YAY!!!!");
			}
			
			System.out.println("Thats all my tests, check these values to ensure correctness");
		} catch (Exception e) {
			System.out.println("Failed to do RMI: " + e);
			e.printStackTrace();
		}
		return;
	}
}
