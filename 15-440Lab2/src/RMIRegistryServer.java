import java.util.concurrent.ConcurrentHashMap;

public class RMIRegistryServer {

	private ConcurrentHashMap<String, RegistryEntry> registry;
    private ServerSocket registrySocket = null;
    private int port;
	
	public void main(String args[]) {
		this.registry = new ConcurrentHashMap<String, RegistryEntry>();
        
        try {
            this.registrySocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + this.port);
            System.exit(-1);
        }
        
        while (true) {
            try {
                processRequest(registrySocket.accept());
                out = new ObjectOutputStream(this.socket.getOutputStream());
                in = new ObjectInputStream(this.socket.getInputStream());
            } catch (IOException e) {
                //TODO: Something here
            }
        }
	}
    
    private void processRequest(Socket socket) {
        ObjectOutputStream out;
        ObjectInputStream in;
        RMIMessage message;
        
        out = new ObjectOutputStream(this.socket.getOutputStream());
        in = new ObjectInputStream(this.socket.getInputStream());
        
        try {
            message = in.readObject();
        } catch (IOException e) {
            //TODO: Stuff
        }
        
        //TODO: get relevant stuff out of message
    }
    
    //TODO: make sure name and entry aren't null in both
    private void bind(String name, RegistryEntry entry) {
        if (this.registry.containsKey(name)) {
            return;  //TODO: what do we send back on this failure?
        }
        this.registry.put(name, entry)
        return;
    }
    
    private void rebind(String name, RegistryEntry entry) {
        this.registry.put(name, entry)
        return;
    }
    
    //TODO: determine return value and how to get it
    private String[] list() {
        
    }
}
