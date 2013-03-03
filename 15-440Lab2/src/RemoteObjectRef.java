import java.io.Serializable;
import java.lang.reflect.Proxy;


public class RemoteObjectRef implements Serializable {

	private String objHost;
	private int objPort;
	private String objName;
	private String objInterface;
	
	public RemoteObjectRef(String host, int port, String name, String interfaceName) {
		super();
		this.objHost = host;
		this.objPort = port;
		this.objName = name;
		this.objInterface = interfaceName;
	}
	
	public Object localise() {
		/* TODO: Make this work, for now just stfu eclipse */
		Object obj = null;
		try {
			return Proxy.newProxyInstance(
					ClassLoader.getSystemClassLoader(),
					new Class<?>[] { Class.forName(this.objInterface) }, 
					new RMIProxyHandler(this.objHost, this.objPort));
		} catch (ClassNotFoundException excpt) {
			System.out.println("Error: Failed to find interface " + this.objInterface);
			return obj;
		} catch (Exception excpt) {
			System.out.println("Error: Failed to localise Remote Object Reference");
			return obj;
		}
	}
	
}
