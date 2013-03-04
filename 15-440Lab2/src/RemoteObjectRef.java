import java.io.Serializable;


public class RemoteObjectRef implements Serializable {

	private static final long serialVersionUID = 8633449110704983682L;
	
	private String objHost;
	private int objPort;
	private String objName;
	private Class<?> objClass;
	private RMIProxy master;
	
	public RemoteObjectRef(String host, int port, String name, Class<?> cls, RMIProxy master) {
		super();
		this.objHost = host;
		this.objPort = port;
		this.objName = name;
		this.objClass = cls;
		this.master = master;
	}

	public Object localise() throws Exception {
		/* Create a proxy for the object, this proxy will handle rmis */
		return RemoteObjectDeref.newProxyInstance(
				this.objClass.getClassLoader(),
				new Class<?>[] { this.objClass.getInterfaces()[0] }, 
				new RMIProxyHandler(this.objHost, this.objPort, this.objName, this.objClass, this.master));
	}
	
}