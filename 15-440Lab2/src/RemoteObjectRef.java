import java.io.Serializable;
import java.lang.reflect.Proxy;


public class RemoteObjectRef implements Serializable {

	private static final long serialVersionUID = 8633449110704983682L;
	
	private String objHost;
	private int objPort;
	private String objName;
	private Class<?> objClass;
	
	public RemoteObjectRef(String host, int port, String name, Class<?> cls) {
		super();
		this.objHost = host;
		this.objPort = port;
		this.objName = name;
		this.objClass = cls;
	}

	public Object localise(RMIProxy localTable) throws Exception {
		/* If the class is an interface, use itself. Otherwiese, get its interfaces */
		Class<?>[] interfaces;
		if (this.objClass.isInterface()) {
			interfaces = new Class<?>[] { this.objClass };
		} else {
			interfaces = this.objClass.getInterfaces();
		}
		
		/* Create a proxy for the object, this proxy will handle rmis */
		return Proxy.newProxyInstance(
				this.objClass.getClassLoader(),
				interfaces, 
				new RMIProxyHandler(this.objHost, this.objPort, this.objName, this.objClass, localTable));
	}
	
}