import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.net.InetAddress;

public class RMIRegistryServer {

	private ConcurrentHashMap<String, RegistryEntry> registry;
    private ServerSocket registrySocket = null;
    private int port;
    private final String usage = "java RMIRegistryServer [port]";
	
	public void main(String args[]) {
		this.registry = new ConcurrentHashMap<String, RegistryEntry>();
        
		if (args.length != 1) {
			System.out.println(usage);
			System.exit(-1);
		}
		
		try {
			port = Integer.parseInt(args[0]);
		} catch (Exception excpt) {
			System.out.println(usage);
			System.exit(-1);
		}
		
        try {
            this.registrySocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + this.port);
            System.exit(-1);
        }
        
        System.out.println("RMI Registry Server Initialized!");
        try {
			System.out.println(" Registry@" + InetAddress.getLocalHost().getHostName() + ":" + port);
		} catch (Exception e) {
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
        
        try {
        	out = new ObjectOutputStream(socket.getOutputStream());
        	in = new ObjectInputStream(socket.getInputStream());
            message = (RegistryMessage) in.readObject();
        } catch (Exception e) {
            System.err.println("Failed to receive message from client");
            return;
        }
        
        RegistryEntry entry = new RegistryEntry();
        entry.host = message.objHost;
        entry.port = message.objPort;
        entry.objClass = message.objClass;
        
        response = new RegistryMessage();
        response.error = null;
        
        if (message.funct.equals("list")) {
            response.regList = list();
        } else if (message.funct.equals("lookup")) {
            response = lookup(message.objName);
        } else if (message.funct.equals("bind")) {
            try {
                bind(message.objName,entry);
            } catch (Exception e) {
                response.error = e;
            }
        } else if (message.funct.equals("rebind")) {
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
            throw new AlreadyBoundException();
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
        RegistryEntry entry = new RegistryEntry();
        if ((entry = this.registry.get(name)) == null) {
            response.error = new NotBoundException();
            return response;
        }
        response.objName = name;
        response.objHost = entry.host;
        response.objPort = entry.port;
        response.objClass = entry.objClass;
        return response;
    }
    
    private String[] list() {
        return (this.registry.keySet()).toArray(new String[0]);
    }
}
