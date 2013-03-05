
public class TestRMIClient {

	public void main(String[] args) {
		/* A simple test of various RMI features */
		RMIRegistryClient myRMI = new RMIRegistryClient(args[0], Integer.parseInt(args[1]));
		
		BasicTests basic;
		try {
			basic = (BasicTests) myRMI.lookup("basic");
		} catch (Exception e) {
			System.out.println("Failed to lookup the object!");
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
		Shipper ship;
		Shipped cargo;
		try {
			adv = (AdvancedTests) myRMI.lookup("adv");
		} catch (Exception e) {
			System.out.println("Failed to lookup the object!");
			return;
		}
		
		/* Pass-by-Ref */
		newTest = adv.makeNewTests("Hello, Advanced!", 42);
		System.out.println(newTest.toString());
		adv.changeNumber(newTest, 10);
		System.out.println(newTest.toString());
		
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
			System.out.println("   We serialized it..... so how.... :(")
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
		
	}
}
