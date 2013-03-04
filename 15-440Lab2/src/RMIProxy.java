import java.util.concurrent.ConcurrentHashMap;


public class RMIProxy implements Runnable {

	private ConcurrentHashMap<String, Object> localObjs;
	private String myHost;
	private int myPort;
    private ServerSocket proxySocket = null;
	
	public RMIProxy(String host) {
		super();
		this.localObjs = new ConcurrentHashMap<String, Object>();
		this.myHost = host;
		this.myPort = 27000; /* Starting port */
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
		return;
	}
	
	public Object findObject(String name) {
		/* Finds and returns an entry of an object with the given name. If the object does exist, return null */
		return this.localObjs.get(name);
	}
	
	public void run() {
		try {
            this.proxySocket = new ServerSocket(this.myPort);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + this.port);
            System.exit(-1);
        }
        while (true) {
            try {
                processRequest(proxySocket.accept());
            } catch (IOException e) {
                //TODO: accept error
            }
        }
	}
    
    private void processRequest(Socket socket) {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        RMIMessage message;
        
        try {
            message = (RMIMessage) in.readObject();
        } catch (IOException e) {
            //TODO: error
        }
        
        Thread slave = new Thread(new RMIProxySlave(this, message, socket));
        slave.start();
    }
}
