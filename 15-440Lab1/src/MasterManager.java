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
        ML.run();
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
        private String[] splitResponse;
        
        ML.slaves.enumerate(slaves);
        
        for (SlaveConnection t : slaves) {
            response = t.messageSlave("BEAT");
            if (response == "Error") {
                continue;
            }
            if (response == "DEAD") {
                t.dead = true;
                continue;
            }
            splitResponse = response.split("#");
            if (splitResponse.length >= 2) {
                for (int i = 1; i < splitResponse.length; i++) {
                    startProcess(splitResponse[i]);
                }
            }
            temp = Integer.parseInt(splitResponse[0]);
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
        this.numSlaves = ML.slaves.activeCount();
    }
    
    // Starts a new process on a random node
    private void startProcess(String process) {
                                                                                // TODO: Find out what you want for pids.
        private int pid = 1234;
        private Thread[] slaves = new Thread[ML.slaves.activeCount()];
        private SlaveConnection target;
        
        ML.slaves.enumerate(slaves);
        target = (SlaveConnection)slaves[(int)(Math.random() * slaves.length)];
        target.messageSlave(pid + " " + process);
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