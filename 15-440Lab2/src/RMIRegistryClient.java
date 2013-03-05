import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class RMIRegistryClient {

	private Thread localProxy;
	private String registryHost;
	private int registryPort;
	private RMIProxy myProxy;
	
	public RMIRegistryClient(String host, int port) throws Exception {
		super();
        ServerSocket proxySocket = null;
		this.registryHost = host;
		this.registryPort = port;
		
		/* Find a port to listen on */
		int myPort;
        for (myPort = 27000; myPort < 27010; myPort++) {
            try {
                proxySocket = new ServerSocket(myPort);
                break;
            } catch (IOException e) {
                if (myPort == 27009) {
                    throw e;
                }
                continue;
            }
        }
        
        /* Create the proxy for all RMIs to this node */
		try {
			this.myProxy = new RMIProxy(InetAddress.getLocalHost().getHostName(), myPort, proxySocket);
			this.localProxy = new Thread(this.myProxy);
			localProxy.start();
		} catch (Exception excpt) {
			throw excpt;
		}
	}
	
	public void bind(String name, Remote440 obj) throws Exception {
        bothBinds(name,obj,"bind");
	}
	
	public void rebind(String name, Remote440 obj) throws Exception {
        bothBinds(name,obj,"rebind");
	}
	
    // Using this because bind and rebind share a lot of code
    private void bothBinds(String name, Remote440 obj, String funct) throws Exception {
        Socket socket;
        ObjectOutputStream out;
        ObjectInputStream in;
        RegistryMessage message, response;
        
		try {
            socket = new Socket(this.registryHost,this.registryPort);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            throw e;
        }
		
        message = new RegistryMessage();
        message.funct = funct;
        message.objName = name;
        message.objHost = this.myProxy.getHost();
        message.objPort = this.myProxy.getPort();
        message.objClass = obj.getClass();
        
        try {
            out.writeObject(message);
            response = (RegistryMessage) in.readObject();
        } catch (IOException e) {
            throw e;
        }
        if (response.error != null) {
            throw response.error;
        }
        out.close();
        socket.close();
        return;
    }
    
	public String[] list() throws Exception{
        Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        RegistryMessage message, response;
        
        try {
            socket = new Socket(this.registryHost,this.registryPort);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            throw e;
        }
        
        message = new RegistryMessage();
        message.funct = "list";
        try {
            out.writeObject(message);
            response = (RegistryMessage) in.readObject();
        } catch (Exception e) {
            throw e;
        }
        
        if (response.error != null) {
            throw response.error;
        } else {
            return response.regList;
        }
	}
	
	public Object lookup(String name) throws Exception {
		Socket socket;
        ObjectInputStream in;
        ObjectOutputStream out;
        RegistryMessage message, response;
        RemoteObjectRef ref;
        
        try {
            socket = new Socket(this.registryHost,this.registryPort);
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw e;
        }
        
        message = new RegistryMessage();
        message.funct = "lookup";
        message.objName = name;
        
        try {
            out.writeObject(message);
            response = (RegistryMessage) in.readObject();
        } catch (IOException e) {
            throw e;
        }
        if (response.error != null) {
            throw response.error;
        } else {
            ref = new RemoteObjectRef(response.objHost,response.objPort,response.objName,response.objClass,myProxy);
            return ref.localise();
        }
	}
	
}
