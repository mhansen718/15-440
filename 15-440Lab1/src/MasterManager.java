import java.util.Arrays;

public class MasterManager implements Runnable {
    private MasterListener ML = null;
    private int numSlaves;
    private int pid = 0;
    
    public MasterManager(MasterListener ML) {
        this.ML = ML;
        ML.run();
        this.numSlaves = 1;
    }
    
    public void run() {
        while (this.numSlaves > 0) {
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("Error: Master interuptted during sleep, heartbeat expedited");
			}
            heartbeat();
        }
        return;
    }
    
    // Checks number of running processes and migrates if needed.
    private void heartbeat() {
        SlaveConnection[] slaveConnectionArray = new SlaveConnection[ML.slaves.activeCount()];
        Thread[] threadArray = new Thread[ML.slaves.activeCount()];
        SlaveConnection freeest = null, busiest = null;
        int fCount = Integer.MAX_VALUE;
        int bCount = 0;
        int temp;
        String response;
        String[] splitResponse;
        
        ML.slaves.enumerate(threadArray);
        
        slaveConnectionArray = Arrays.copyOf(threadArray, threadArray.length, SlaveConnection[].class);
        
        for (SlaveConnection t : slaveConnectionArray) {
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
        SlaveConnection[] slaves = new SlaveConnection[ML.slaves.activeCount()];
        Thread[] threads = new Thread[ML.slaves.activeCount()];
        SlaveConnection target;
        
        ML.slaves.enumerate(threads);
        
        slaves = Arrays.copyOf(threads, threads.length, SlaveConnection[].class);
        
        target = slaves[(int)(Math.random() * slaves.length)];
        target.messageSlave((this.pid++) + " " + process);
    }
    
    //Migrates process, if it fails to restart it tries again on up to 5 random slaves
    private void migrate(SlaveConnection source, SlaveConnection dest) {
        String sourceResponse;
        String destResponse;
        int tries = 0;
        SlaveConnection[] slaves = new SlaveConnection[ML.slaves.activeCount()];
        Thread[] threads = new  Thread[ML.slaves.activeCount()];
        
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
                    ML.slaves.enumerate(threads);
                    
                    slaves = Arrays.copyOf(threads, threads.length, SlaveConnection[].class);
                    
                }
                dest = (SlaveConnection)slaves[(int)(Math.random() * slaves.length)];
            }
            destResponse = dest.messageSlave("PLANT" + sourceResponse);
        }
    }
}