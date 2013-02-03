import java.net.*
import java.io.*

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
    private PrintWriter out = null;
    private BufferedReader in = null;
    public Boolean dead = false;
    
    public SlaveConnection(Socket socket) {
        this.socket = socket;
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public void run() {
        while (!dead) {
            
        }
    }
    
    public String messageSlave(String msg) {
        String response = "";
        String input;
        try {
            out.println(msg);
            while ((input = in.readLine()) != "END") {
                response += input;
            }
            return response;
        } catch (IOException e) {
            return "Error";
        }
    }
}

public class MasterManager implements Runnable {
    private MasterListener = null;
    private numSlaves;
    
    public MasterManager(MasterListener ML) {
        this.MasterListener = ML;
        this.numSlaves = 1;
    }
    
    public void run() {
        while (this.numSlaves > 0) {
            sleep(5000);
            heartbeat();
        }
        return;
    }
    
    // Checks number of running processes and migrates if needed.
    private void heartbeat() {
        private Thread[] slaves = new Thread[ML.slaves.activeCount()];
        private Thread freeest, busiest;
        private int fCount = Integer.MAX_VALUE;
        private int bCount = 0;
        private int temp;
        private String response;
        
        ML.slaves.enumerate(slaves);
        
        for (SlaveConnection t : slaves) {
            try {
                response = t.messageSlave("BEAT");
            } catch (IOException e) {
                continue;
            }
            if (response == "DEAD") {
                t.dead = true;
                this.numSlaves--;
                continue;
            }
            temp = Integer.parseInt(response);
            if (temp < fCount) {
                fCount = temp;
                freeest = t;
            } else if (temp > bCount) {
                bCount = temp;
                busiest = t;
            }
        }
        if (bCount - fCount >= 2) {
            migrate(busiest,freeest);
        }
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