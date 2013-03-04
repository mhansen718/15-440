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
        RegistryMessage message;
        
        out = new ObjectOutputStream(this.socket.getOutputStream());
        in = new ObjectInputStream(this.socket.getInputStream());
        
        try {
            message = in.readObject();
        } catch (IOException e) {
            //TODO: Stuff
        }
        
        if (message.funct == "list") {
            RegistryMessage response = new RegistryMessage();
            response.regList = list();
            try {
                out.writeObject(response);
            } catch (exception e) {  //TODO: errors
                
            }
            return;
        } else if (message.funct == "lookup") {
            try {
                out.writeObject(lookup(message.objName));
            } catch (exception e) { // TODO: errors
                
            }
        }
        RegistryEntry entry = new RegistryEntry();
        entry.host = message.objHost;
        entry.port = message.objPort;
        entry.interfaceNames = message.objInterfaces;
        if (message.funct == "bind") {
            bind(message.objName,entry);
        } else if (message.funct == "rebind") {
            rebind(message.objName,entry);
        } else {
            //TODO: No valid command
        }
        try {
            socket.close();
        } catch (IOException e) {
            //TODO: Well fuck, you failed to close a socket
        }
        return;
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
    
    private RegistryMessage lookup(String name) {
        RegistryMessage response = new RegistryMessage();
        if ((RegistryEntry entry = this.registry.get(name)) == null) {
            return response;  //TODO: objectnotfound error
        }
        response.objName = name;
        response.objHost = entry.host;
        response.objPort = entry.port;
        response.objInterfaces = entry.interfaceNames;
        return response;
    }
    
    private String[] list() {
        return (this.registry.keySet()).toArray();
    }
}
