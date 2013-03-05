
public class TestRMIServer {

	public static void main(String[] args) {
		/* A host for the objects TestRMIClient will call on */
		BasicTestsImpl basic = new BasicTestsImpl("Hi", 4);
		AdvancedTestsImpl adv = new AdvancedTestsImpl();
		RMIRegistryClient myRMI;
		try {
			myRMI = new RMIRegistryClient(args[0], Integer.parseInt(args[1]));
			
			myRMI.bind("basics", adv);
			myRMI.bind("adv", adv);
			myRMI.rebind("basics", basic);
			
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