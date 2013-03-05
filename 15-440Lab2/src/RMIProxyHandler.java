import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
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
	
	public RemoteObjectRef makeRemoteObjectRef() {
		RemoteObjectRef ref = new RemoteObjectRef(this.host, this.port, this.name, this.cls);
		return ref;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable { 
		RMIMessage sent = new RMIMessage();
		RMIMessage received;
		Object[] trueArgs;
		Object returnObj;
		int idx = 0;
		RemoteObjectRef remoteArg;
		String remoteArgName;
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
		
		/* Ensure all wanted arguments are serializable */
        if (args != null) {
        	trueArgs = new Object[args.length];
        	for (Object arg : args) {
        		if (!(arg instanceof Serializable)) {
        			throw new RemoteException();
        		}
        		
        		if (Proxy.isProxyClass(arg.getClass()) && 
				(Proxy.getInvocationHandler(arg)).getClass().equals(RMIProxyHandler.class)) {
        			System.out.println("Its a reference!, make a new one!");
        			remoteArg = ((RMIProxyHandler) Proxy.getInvocationHandler(arg)).makeRemoteObjectRef();
        			trueArgs[idx] = remoteArg;
        		} else if (arg instanceof Remote440) {
        			System.out.println("Packing....");
        			remoteArgName = Integer.toString(arg.hashCode());
        			remoteArg = new RemoteObjectRef(this.master.getHost(), this.master.getPort(), 
        					remoteArgName, arg.getClass());
        			this.master.addObject(remoteArgName, arg);
        			trueArgs[idx] = remoteArg;
        		} else {
        			trueArgs[idx] = arg;
        		}
        		idx++;
        	}
        } else {
        	trueArgs = new Object[0];
        }
		
		/* Load up message to send */
		sent.name = this.name;
		sent.methodName = method.getName();
		sent.parameterTypes = method.getParameterTypes();
		sent.cls = this.cls;
		sent.args = trueArgs;
		sent.returnValue = null;
		sent.exception = null;
		
		System.out.println("Im sending!!!");
		try {
			socket = new Socket(this.host,this.port);
			System.out.println("Connected!!!");
			out = new ObjectOutputStream(socket.getOutputStream());
			System.out.println("Here i go");
            out.writeObject(sent);
            System.out.println("I think i sent...");
            in = new ObjectInputStream(socket.getInputStream());
            received = (RMIMessage) in.readObject();
        } catch (Exception e) {
        	System.out.println("OH NO! " + e);
            throw new RemoteException("Communication failure");
        }
        System.out.println("I got it!!!");
		/* Check to see if the received method resulted in an exception. If so, throw. */
		if (received.exception != null) {
			throw received.exception;
		}
		
		/* If no exception, localise remote references or just pass the value through */
		if (Remote440.class.isAssignableFrom(method.getReturnType())) {
			returnObj = ((RemoteObjectRef) received.returnValue).localise(this.master);
		} else {
			returnObj = received.returnValue;
		}
		
		return returnObj;
	}
}