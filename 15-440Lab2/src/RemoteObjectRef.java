import java.io.Serializable;
import java.lang.reflect.Proxy;


public class RemoteObjectRef implements Serializable {

	private static final long serialVersionUID = 8633449110704983682L;
	
	private String objHost;
	private int objPort;
	private String objName;
	private String objInterface;
	private RMIProxy master;
	
	public RemoteObjectRef(String host, int port, String name, String interfaceName, RMIProxy master) {
		super();
		this.objHost = host;
		this.objPort = port;
		this.objName = name;
		this.objInterface = interfaceName;
		this.master = master;
	}
	
	public Object localise() {
		/* Create a proxy for the object, this proxy will handle rmis */
		Object obj = null;

		try {
			Class<?> objClass = Class.forName(this.objInterface);
			return Proxy.newProxyInstance(
					ClassLoader.getSystemClassLoader(),
					new Class<?>[] { objClass }, 
					new RMIProxyHandler(this.objHost, this.objPort, this.objName, objClass, this.master));
		} catch (ClassNotFoundException excpt) {
			System.out.println("Error: Failed to find interface " + this.objInterface);
			return obj;
		} catch (Exception excpt) {
			System.out.println("Error: Failed to localise Remote Object Reference");
			return obj;
		}
	}
	
}
