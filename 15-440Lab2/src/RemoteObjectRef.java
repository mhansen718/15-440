import java.io.Serializable;
import java.lang.reflect.Proxy;


public class RemoteObjectRef implements Serializable {

	private static final long serialVersionUID = 8633449110704983682L;
	
	private String objHost;
	private int objPort;
	private String objName;
	private String objInterface;
	private RMIProxy master;
	
    //TODO: Change interfaceName to be a Class<?>
	public RemoteObjectRef(String host, int port, String name, String interfaceName, RMIProxy master) {
		super();
		this.objHost = host;
		this.objPort = port;
		this.objName = name;
		this.objInterface = interfaceName;
		this.master = master;
	}

	public Object localise() throws Exception {
		/* Create a proxy for the object, this proxy will handle rmis */
		Class<?> objClass = Class.forName(this.objInterface);
		return RemoteObjectDeref.newProxyInstance(
				ClassLoader.getSystemClassLoader(),
				new Class<?>[] { objClass }, 
				new RMIProxyHandler(this.objHost, this.objPort, this.objName, objClass, this.master));
	}
	
}
