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
	
	public RMIRegistryClient(String host, int port) throws Exception {
		super();
        ServerSocket proxySocket;
		this.registryHost = host;
		this.registryPort = port;
		
		/* Create the proxy for all RMIs to this node */
        for (int i = 27000; i<27010; i++) {
            try {
                proxySocket = new ServerSocket(port);
                break;
            } catch (IOException e) {
                if (i == 27009) {
                    throw IOException e;
                }
                continue;
            }
        }
		try {
			this.myProxy = new RMIProxy(InetAddress.getLocalHost().getHostName(), i, proxySocket);
			this.localProxy = new Thread(this.myProxy);
			localProxy.start();
		} catch (Exception excpt) {
			throw excpt;
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
        RegistryMessage message, response;
		try {
            socket = new Socket(this.registryHost,this.registryPort);
            out = new ObjectOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            System.err.println("Failed to open connection to server");
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
            System.err.println("Failed to communicate with server");
            return;
        }
        if (response.error) {
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
        } catch (IOException e) {
            System.err.println("Failed to open connection to server");
            return;
        }
        message = new RegistryMessage();
        message.funct = "list";
        try {
            out.writeObject(message);
            response = (RegistryMessage) in.readObject();
        } catch (IOException e) {
            System.err.println("Failed to communicate with server");
            return;
        }
        if (response.error) {
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
            System.err.println("Failed to open connection to server");
            return;
        }
        message = new RegistryMessage();
        message.funct = "lookup";
        message.objName = name;
        try {
            out.writeObject(message);
            response = (RegistryMessage) in.readObject();
        } catch (IOException e) {
            System.err.println("Failed to communicate with server");
            return;
        }
        if (response.error) {
            throw response.error;
        } else {
            ref = new RemoteObjectRef(response.objHost,response.objPort,response.objName,response.objClass,myProxy);
            return ref.localise();
        }
	}
	
}
