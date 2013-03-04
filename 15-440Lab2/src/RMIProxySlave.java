import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.RemoteException;


public class RMIProxySlave implements Runnable {

	private RMIMessage message;
	private RMIProxy master;
	
	public RMIProxySlave(RMIProxy master, RMIMessage message) {
		super();
		this.master = master;
		this.message = message;
	}
	
	public void run() {
		RMIMessage returnMessage = new RMIMessage();
		Method method;
		Object foundObj;
		Object returnObj;
		
		returnMessage.name = message.name;
		returnMessage.methodName = message.methodName;
		returnMessage.parameterTypes = message.parameterTypes;
		returnMessage.cls = message.cls;
		returnMessage.args = message.args;
		
		/* Try to find the object, if we dont know about it, return a Remote Exception */
		foundObj = this.master.findObject(message.name);
		if (foundObj == null) {
			returnMessage.exception = new RemoteException();
			sendMessage(returnMessage);
			return;
		}

		try {
			/* Revive the method */
			method = message.cls.getMethod(message.methodName, message.parameterTypes);
			returnObj = method.invoke(foundObj, message.args);
			
			if (Remote440.class.isAssignableFrom(method.getReturnType())) {
				/* If this class is a Remote440 object, package it up as a remote object reference with a unique name */
				String newName = Integer.toString(returnObj.hashCode());
				RemoteObjectRef newRemote = new RemoteObjectRef(this.master.getHost(), this.master.getPort(), 
						newName, method.getReturnType().getName(), this.master);
				this.master.addObject(newName, returnObj);
				returnMessage.returnValue = newRemote;
				returnMessage.exception = null;
			} else if (Serializable.class.isAssignableFrom(method.getReturnType())) {
				/* If this class is a Remote440 object, package it up as a whole object */
				returnMessage.returnValue = returnObj;
				returnMessage.exception = null;
			} else {
				/* We cant return it, so send remote exception and done */
				returnMessage.exception = new RemoteException();
			}
		} catch (Exception excpt) {
			returnMessage.exception = excpt;
		}
		
		sendMessage(returnMessage);
		return;
	}
		
	private void sendMessage(RMIMessage message) {
		/* TODO: Make this method send the message back to the rmi caller */
		return;
	}
}
