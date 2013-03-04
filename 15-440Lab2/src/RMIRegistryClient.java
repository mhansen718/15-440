import java.net.InetAddress;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class RMIRegistryClient {

	private Thread localProxy;
	private String registryHost;
	private int registryPort;
	private RMIProxy myProxy;
	
	public RMIRegistryClient(String host, int port) {
		super();
		this.registryHost = host;
		this.registryPort = port;
		
		/* Create the proxy for all RMIs to this node */
		try {
			this.myProxy = new RMIProxy(InetAddress.getLocalHost().getHostName());
			this.localProxy = new Thread(this.myProxy);
			localProxy.start();
		} catch (Exception excpt) {
			System.out.println("Error: Failed to set up Proxy for RMI");
		}
	}
	
	public void bind(String name, Remote440 obj) {
        bothBinds(name,obj,"bind");
	}
	
	public void rebind(String name, Remote440 obj) {
        bothBinds(name,obj,"rebind");
	}
	
    // Using this because bind and rebind share a lot of code
    private void bothBinds(String name, Remote440 obj, String funct) {
        Socket socket;
        ObjectOutputStream out;
        RegistryMessage message;
		try {
            this.socket = new Socket(this.registryHost,this.registryPort);
            out = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            //TODO: error
        }
        message = new RegistryMessage();
        message.funct = funct;
        message.objName = name;
        message.objHost = this.myProxy.getHost();
        message.objPort = this.myProxy.getPort();
        message.objInterface = "How do I get this again?"; //TODO
        try {
            out.writeObject(message);
        } catch (IOException e) {
            //TODO: you know by now
        }
        return;
    }
    
	public String[] list(String name) {
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        RegistryMessage message, response;
        try {
            socket = new Socket(this.registryHost,this.registryPort);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            //TODO: error
        }
        message = new RegistryMessage();
        message.funct = "list";
        try {
            out.writeObject(message);
            response = (RegistryMessage) in.readObject();
        } catch (IOException e) {
            //TODO: second verse, same as the first
        }
        return response.regList;
	}
	
	public Remote440 lookup(String name) {
		Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        RegistryMessage message, response;
        try {
            socket = new Socket(this.registryHost,this.registryPort);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            //TODO: error
        }
        message = new RegistryMessage();
        message.funct = "lookup";
        message.objName = name;
        try {
            out.writeObject(message);
            response = (RegistryMessage) in.readObject();
        } catch (IOException e) {
            //TODO: Yep.
        }
        //TODO: make the right return value out of that
	}
	
}
