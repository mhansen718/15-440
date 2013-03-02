import java.io.Serializable;


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
		Object o = new Object();
		return o;
	}
	
}
