import java.io.IOException;
import java.net.ServerSocket;

public class MasterListener implements Runnable {
    private ServerSocket serverSocket = null;
    private final int port;
    private Set<SlaveConnection> slaves;
    private ReentrantLock lock;
    
    public MasterListener(int port,ReentrantLock lock) {
        super();
        this.port = port;
        this.slaves = new HashSet<SlaveConnection>();
        this.lock = lock;
    }
    
    public Set<SlaveConnection> getSlaves() {
        return slaves;
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
                Thread newThread = new Thread(new SlaveConnection(serverSocket.accept()))
                this.lock.lock();
        		this.slaves.add(newThread);
                this.lock.unlock();
                newThread.start();
        	} catch (IOException e) {
        		System.err.println("Error accepting connection from slave.");
        	}
        }
    }
}
