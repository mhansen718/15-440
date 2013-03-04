import java.net.InetAddress;


public class RMIRegistryClient {

	private Thread localProxy;
	private String registryHost;
	private int registryPort;
	private RMIProxy myProxy;
	
	public RMIRegistryClient(String host, int port) {
		super();
		this.registryHost = host;
		this.registryPort = port;
		
		/* Create the proxy for all RMIs to this node */
		try {
			this.myProxy = new RMIProxy(InetAddress.getLocalHost().getHostName());
			this.localProxy = new Thread(this.myProxy);
			localProxy.start();
		} catch (Exception excpt) {
			System.out.println("Error: Failed to set up Proxy for RMI");
		}
	}
	
	public static void bind(String name, Remote440 obj) {
		/* TODO: Make this go to registry and send its stuff */
	}
	
	public static void rebind(String name, Remote440 obj) {
		/* TODO: Same but without caring about existing object of same name */
	}
	
	public static String[] list(String name) {
		/* TODO: Return list of all objects in register (see java.rmi.Naming) */
		String[] objList = {"HI", "YO"}; // Make Eclipse Happy
		
		return objList;
	}
	
	public static Remote440 lookup(String name) {
		/* TODO: Make this work */
		Remote440 foundObj = (Remote440) new RemoteObjectRef(); // Make Eclipse Happy
		
		return foundObj;
	}
	
}
