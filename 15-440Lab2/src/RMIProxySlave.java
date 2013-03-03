import java.io.Serializable;
import java.lang.reflect.Proxy;
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
		Object foundObj;
		
		returnMessage.name = message.name;
		returnMessage.method = message.method;
		returnMessage.args = message.args;
		
		/* Try to find the object, if we dont know about it, return a Remote Exception */
		foundObj = master.findObject(message.name);
		if (foundObj == null) {
			RemoteException excpt = new RemoteException();
			returnMessage.exception = excpt;
			sendMessage(returnMessage);
			return;
		}
		
		try {
			returnMessage.returnValue = message.method.invoke(foundObj, message.args);
			if (Remote440.class.isAssignableFrom(message.method.getReturnType())) {
				/* If this class is a Remote440 object, package it up as a remote object reference */
				
			} else if (Serializable.class.isAssignableFrom(message.method.getReturnType())) {
				/* If this class is a Remote440 object, package it up as a whole object */
			} else {
				/* We cant return it, so send remote exception and done */
				RemoteException excpt = new RemoteException();
				returnMessage.exception = excpt;
			}
		} catch (Exception excpt) {
			returnMessage.exception = excpt;
		}
		
		returnMessage.exception = null;
		sendMessage(returnMessage);
		return;
	}
		
	private void sendMessage(RMIMessage message) {
		/* TODO: Make this method send the message back to the rmi caller */
		return;
	}
}
