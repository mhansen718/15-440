import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.RemoteException;


public class RMIProxyHandler implements InvocationHandler {

	private String host;
	private int port;
	private String name;
	private Class<?> cls;
	private RMIProxy master;
	
	public RMIProxyHandler(String host, int port, String name, Class<?> cls, RMIProxy master) {
		super();
		this.host = host;
		this.port = port;
		this.name = name;
		this.cls = cls;
		this.master = master;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable { 
		RMIMessage sent = new RMIMessage();
		RMIMessage received;
		Object returnObj;
		Object[] trueArgs = new Object[args.length];
		int idx = 0;
		RemoteObjectRef remoteArg;
		String remoteArgName;
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
		
		/* Ensure all wanted arguments are serializable */
		for (Object arg : args) {
			if (!(arg instanceof Serializable)) {
				throw new RemoteException();
			}
			if ((arg instanceof Remote440) && !(arg instanceof RemoteObjectDeref)) {
				remoteArgName = Integer.toString(arg.hashCode());
				remoteArg = new RemoteObjectRef(this.master.getHost(), this.master.getPort(), 
						remoteArgName, arg.getClass(), this.master);
				this.master.addObject(remoteArgName, arg);
				trueArgs[idx] = remoteArg;
			} else {
				trueArgs[idx] = arg;
			}
			idx++;
		}
		
		/* Load up message to send */
		sent.name = this.name;
		sent.methodName = method.getName();
		sent.parameterTypes = method.getParameterTypes();
		sent.cls = this.cls;
		sent.args = trueArgs;
		sent.returnValue = null;
		sent.exception = null;
		
        socket = new Socket(this.host,this.port);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        
        try {
            out.writeObject(sent);
            received = (RMIMessage) in.readObject();
        } catch (IOException e) {
            System.err.println("Failed to communicate with object");
            return;
        }
        
		/* Check to see if the received method resulted in an exception. If so, throw. */
		if (received.exception != null) {
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