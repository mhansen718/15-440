import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class RMIProxyHandler implements InvocationHandler {

	private String host;
	private int port;
	
	public RMIProxyHandler(String host, int port) {
		super();
		this.host = host;
		this.port = port;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable { 
		/* TODO: Package up method, name and args into RMIMessage and send them to host:port
		 * Then, wait for response and on reponse, either localise remote ref or give out the object itself */

		Object o = new Object();
		return o;
	}

}
