import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


public class RMIProxy implements Runnable {

	private ConcurrentHashMap<String, Object> localObjs;
	private String myHost;
	private int myPort;
    private ServerSocket proxySocket;
	
	public RMIProxy(String host, int port, ServerSocket proxySocket) {
		super();
		this.localObjs = new ConcurrentHashMap<String, Object>();
		this.myHost = host;
		this.myPort = port;
        this.proxySocket = proxySocket;
	}
	
	public String getHost() {
		/* Get the host name for this node */
		return this.myHost;
	}
	
	public int getPort() {
		/* Get the listening port for this node */
		return this.myPort;
	}
	
	public void addObject(String name, Object newObj) {
		/* This function simply adds a new object to the set. If it is in the set already, remap */
		this.localObjs.put(name, newObj);
		System.out.println(this.localObjs.toString());
		return;
	}
	
	public Object findObject(String name) {
		/* Finds and returns an entry of an object with the given name. If the object does exist, return null */
		return this.localObjs.get(name);
	}
	
	public void run() {
        while (true) {
            try {
                processRequest(proxySocket.accept());
            } catch (Exception e) {
                continue;
            }
        }
	}
    
    private void processRequest(Socket socket) {
        RMIMessage message;
        System.out.println("got connection");
        try {
        	ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            message = (RMIMessage) in.readObject();
        } catch (Exception e) {
            return;
        }
        
        try {
        	System.out.println("Starting now");
        	Thread slave = new Thread(new RMIProxySlave(this, message, socket));
        	slave.start();
        } catch (Exception e) {
        	return;
        }
    }
}
