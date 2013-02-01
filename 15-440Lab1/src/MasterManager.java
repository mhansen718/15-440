import java.net.*

public class MasterListener implements Runnable {
    
    private ServerSocket serverSocket = null;
    private final int port;
    public ThreadGroup slaves;
    
    public MasterManager(int port) {
        super();
        this.port = port;
        this.slaves = new ThreadGroup("Slaves");
    }
    
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOexception e) {
            System.err.println("Could not listen on port: " + this.port);
            System.exit(-1);
        }
        
        while (true) {
            new Thread(this.slaves, new SlaveConnection(serverSocket.accept())).start();
        }
    }
}

public class SlaveConnection implements Runnable {
    
    private Socket socket = null;
    
    public SlaveConnection(Socket socket) {
        this.socket = socket;
    }
    
    public void run() {
        
    }
    
    public String messageSlave(String msg) {
        
    }
}

public class MasterManager implements Runnable {
    
    private MasterListener = null;
    
    public MasterManager(MasterListener ML) {
        this.MasterListener = ML;
    }
    
    public void run() {
        
    }
    
    private void heartbeat() {
        
    }
    
    //Migrates process, if it fails to restart it tries again on up to 5 random slaves
    private void migrate(SlaveConnection source, SlaveConnection dest) {
        private String sourceResponse;
        private String destResponse;
        private int tries = 0;
        private Thread[] slaves = new Thread[ML.slaves.activeCount()];
        
        sourceResponse = source.messageSlave("PLOP");
        if (sourceResponse == "Error") {
            System.err.println("Error serializing process");
            return;
        }
        destResponse = dest.messageSlave("PLANT" + sourceResponse);
        while (destResponse == "Error") {
            System.err.println("Error restarting process");
            tries++;
            if (tries >= 30) {
                System.err.println("Giving up on that process, sorry");
                return;
            } else if (tries % 5 == 0) {
                System.err.println("Trying different node");
                if (slaves[0] == null) {
                    ML.slaves.enumerate(slaves);
                }
                dest = (SlaveConnection)slaves[(int)(Math.random() * slaves.length)];
            }
            destResponse = dest.messageSlave("PLANT" + sourceResponse);
        }
    }
}