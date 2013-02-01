import java.net.*

public class MasterManager implements Runnable {
    
    private ServerSocket serverSocket = null;
    private final int port;
    
    public MasterManager(int port) {
        super();
        this.port = port;
    }
    
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOexception e) {
            System.err.println("Could not listen on port: " + this.port);
            System.exit(-1);
        }
    }
}