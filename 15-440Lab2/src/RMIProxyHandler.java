import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;


public class RMIProxyHandler implements InvocationHandler {

	private String host;
	private int port;
	private String name;
	private Class<?> cls;
	
	public RMIProxyHandler(String host, int port, String name, Class<?> cls) {
		super();
		this.host = host;
		this.port = port;
		this.name = name;
		this.cls = cls;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable { 
		/* TODO: Package up method, name and args into RMIMessage and send them to host:port
		 * Then, wait for response and on reponse, either localise remote ref or give out the object itself */
		RMIMessage sent = new RMIMessage();
		RMIMessage received;
		Object returnObj;
		
		/* Ensure all arguments are serializable */
		for (Class<?> paramClass : method.getParameterTypes()) {
			if (Serializable.class.isAssignableFrom(paramClass)) {
				throw new RemoteException();
			}
		}
		
		/* Load up message to send */
		sent.name = this.name;
		sent.methodName = method.getName();
		sent.parameterTypes = method.getParameterTypes();
		sent.cls = this.cls;
		sent.args = args;
		sent.returnValue = null;
		sent.exception = null;
		
		/* Check to see if the received method resulted in an exception. If so, throw. */
		if (received.exception == null) {
			throw received.exception;
		}
		
		/* If no exception, localise remote references or just pass the value through */
		if (Remote440.class.isAssignableFrom(method.getReturnType())) {
			returnObj = ((RemoteObjectRef) received.returnValue).localise();
		} else {
			returnObj = received.returnValue;
		}
		
		return returnObj;
	}

}
