import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

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
        		System.out.println("I r listening");
                SlaveConnection connection = new SlaveConnection(serverSocket.accept());
                System.out.println("I got one bitch");
                Thread newThread = new Thread(connection);
                this.lock.lock();
        		this.slaves.add(connection);
                this.lock.unlock();
                newThread.start();
                System.out.println("Got new connection");
        	} catch (IOException e) {
        		System.out.println();
        		System.err.println("Master Error: Fail to accept connection from slave");
        		System.out.println("->>");
        	}
        }
    }
}
