import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.rmi.RemoteException;


public class RMIProxySlave implements Runnable {

	private RMIMessage message;
	private RMIProxy master;
    private Socket socket;
	
	public RMIProxySlave(RMIProxy master, RMIMessage message, Socket socket) {
		super();
		this.master = master;
		this.message = message;
        this.socket = socket;
	}
	
	public void run() {
		RMIMessage returnMessage = new RMIMessage();
		Method method;
		RemoteObjectRef newRemote;
		Object foundObj;
		Object returnObj;
		Object[] trueArgs = new Object[message.args.length];
		int idx = 0;
		
		returnMessage.name = message.name;
		returnMessage.methodName = message.methodName;
		returnMessage.parameterTypes = message.parameterTypes;
		returnMessage.args = message.args;
		
		/* Localise Any Remote Objects */
		for (Object arg : message.args) {
			if (arg instanceof RemoteObjectRef) {
				try {
					trueArgs[idx] = ((RemoteObjectRef) arg).localise(this.master);
				} catch (Exception excpt) {
					returnMessage.exception = excpt;
					sendMessage(returnMessage);
					return;
				}
			} else {
				trueArgs[idx] = arg;
			}
			idx++;
		}
		
		/* Try to find the object, if we dont know about it, return a Remote Exception */
		foundObj = this.master.findObject(message.name);
		if (foundObj == null) {
			returnMessage.exception = new RemoteException();
			sendMessage(returnMessage);
			return;
		}

		try {
			/* Revive and invoke the method */
			method = (foundObj.getClass()).getMethod(message.methodName, message.parameterTypes);
			try {
				returnObj = method.invoke(foundObj, trueArgs);
			} catch(InvocationTargetException excpt) {
				returnMessage.exception = (Exception) excpt.getCause();
				sendMessage(returnMessage);
				return;
			} catch(IllegalArgumentException excpt) {
				returnMessage.exception = excpt;
				sendMessage(returnMessage);
				return;
			}
			
			if (!(returnObj == null) && Proxy.isProxyClass(returnObj.getClass()) && 
					(Proxy.getInvocationHandler(returnObj)).getClass().equals(RMIProxyHandler.class)) {
				/* If the return object is a remote object ref, rereference and put in message */
				newRemote = ((RMIProxyHandler) Proxy.getInvocationHandler(returnObj)).makeRemoteObjectRef();
				returnMessage.returnValue = newRemote;
				returnMessage.exception = null;
			} else if (returnObj instanceof Remote440) {
				/* If the return value is a Remote440 object, package it up as a remote object reference with a unique name */
				String newName = Integer.toString(returnObj.hashCode());
				newRemote = new RemoteObjectRef(this.master.getHost(), this.master.getPort(), 
						newName, method.getReturnType());
				this.master.addObject(newName, returnObj);
				returnMessage.returnValue = newRemote;
				returnMessage.exception = null;
			} else if ((returnObj instanceof Serializable) || (returnObj == null)) {
				/* If the return value is Serializable object, package it up as a whole object */
				returnMessage.returnValue = returnObj;
				returnMessage.exception = null;
			} else {
				/* We cant return it, so send remote exception and done */
				returnMessage.exception = new RemoteException();
			}
		} catch (Exception excpt) {
			/* If theres some unexpected problem during invocation and packaging */
			returnMessage.exception = new RemoteException();
		}
		
		sendMessage(returnMessage);
		return;
	}
		
	private void sendMessage(RMIMessage message) {
        try {
        	ObjectOutputStream out = new ObjectOutputStream(this.socket.getOutputStream());
            out.writeObject(message);
        } catch (Exception e) {
        	/* Any problems here, we just have to give up.... */
            return;
        }
		return;
	}
}