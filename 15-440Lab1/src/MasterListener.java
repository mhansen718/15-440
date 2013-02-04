import java.io.IOException;
import java.net.ServerSocket;

public class MasterListener implements Runnable {
    private ServerSocket serverSocket = null;
    private final int port;
    public ThreadGroup slaves;
    
    public MasterListener(int port) {
        super();
        this.port = port;
        this.slaves = new ThreadGroup("Slaves");
    }
    
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + this.port);
            System.exit(-1);
        }
        
        while (true) {
        	try {
        		new Thread(this.slaves, new SlaveConnection(serverSocket.accept())).start();
        	} catch (IOException e) {
        		System.err.println("Error accepting connection from slave.");
        	}
        }
    }
}
