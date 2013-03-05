import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.AlreadyBoundException;

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
            } catch (IOException e) {
                System.err.println("Accept error");
                continue;
            }
        }
	}
    
    private void processRequest(Socket socket) {
        ObjectOutputStream out;
        ObjectInputStream in;
        RegistryMessage message, response;
        
        out = new ObjectOutputStream(this.socket.getOutputStream());
        in = new ObjectInputStream(this.socket.getInputStream());
        
        try {
            message = (RegistryMessage) in.readObject();
        } catch (IOException e) {
            System.err.println("Failed to receive message from client");
            return;
        }
        
        response = new RegistryMessage();
        response.error = null;
        if (message.funct == "list") {
            response.regList = list();
        } else if (message.funct == "lookup") {
            response = lookup(message.objName);
        }
        RegistryEntry entry = new RegistryEntry();
        entry.host = message.objHost;
        entry.port = message.objPort;
        if (message.funct == "bind") {
            try {
                bind(message.objName,entry);
            } catch (Exception e) {
                response.error = e;
            }
        } else if (message.funct == "rebind") {
            try {
                rebind(message.objName,entry);
            } catch (NullPointerException e) {
                response.error = e;
            }
        } else {
            response.error = new RemoteException("Invalid Command");
        }
        try {
            out.writeObject(response);
        } catch (IOException e) {
            System.err.println("Failed to communicate with client");
            return;
        }
        try {
            out.close();
            in.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("How did you fail to close a socket?");
        }
        return;
    }
    
    private void bind(String name, RegistryEntry entry) throws Exception {
        if (this.registry.containsKey(name)) {
            throw AlreadyBoundException;
        }
        try {
            this.registry.put(name, entry);
        } catch (NullPointerException e) {
            throw e;
        }
        return;
    }
    
    private void rebind(String name, RegistryEntry entry) {
        try {
            this.registry.put(name, entry);
        } catch (NullPointerException e) {
            throw e;
        }
        return;
    }
    
    private RegistryMessage lookup(String name) {
        RegistryMessage response = new RegistryMessage();
        if ((RegistryEntry entry = this.registry.get(name)) == null) {
            response.error = NotBoundException;
            return response;
        }
        response.objName = name;
        response.objHost = entry.host;
        response.objPort = entry.port;
        response.objClass = entry.objClass;
        return response;
    }
    
    private String[] list() {
        return (this.registry.keySet()).toArray();
    }
}
