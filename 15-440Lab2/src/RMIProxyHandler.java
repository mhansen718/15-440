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
		RMIMessage sent = new RMIMessage();
		RMIMessage received;
		Object returnObj;
		
		/* Check to see if the received method resulted in an exception. If so, throw. */
		if (received.exception == null) {
			throw received.exception;
		}
		
		/* If no exception, localise remote references or just pass the value through */
		if (Remote440.class.isAssignableFrom(received.method.getReturnType())) {
			returnObj = ((RemoteObjectRef) received.returnValue).localise();
		} else {
			returnObj = received.returnValue;
		}
		
		return returnObj;
	}

}
