import java.util.Iterator;
import java.util.concurrent.locks.ReentrantLock;

public class MasterManager implements Runnable {
    private MasterListener ML = null;
    private int pid = 0;
    private boolean noSlaves = false;
    private ReentrantLock lock;
    
    public MasterManager(MasterListener ML,ReentrantLock lock) {
        this.ML = ML;
        new Thread(ML).start();
        this.lock = lock;
    }
    
    public void run() {
        while (!this.noSlaves) {
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				System.out.println("Master Error: Interrupted during sleep, heartbeat expedited");
			}
            heartbeat();
        }
        return;
    }
    
    // Checks number of running processes and migrates if needed.
    private void heartbeat() {
        Iterator<SlaveConnection> iterator;
        SlaveConnection freeest = null, busiest = null;
        int fCount = Integer.MAX_VALUE;
        int bCount = 0;
        int temp = 0;
        String response;
        String[] splitResponse;
        
        this.lock.lock();
        iterator = (ML.getSlaves()).iterator();
        
        while (iterator.hasNext()) {
            SlaveConnection t = iterator.next();
            response = t.messageSlave("BEAT");
            if (response.equals("Error")) {
                iterator.remove();
                continue;
            }
            splitResponse = response.split("#");
            if (splitResponse.length >= 2) {
                for (int i = 1; i < splitResponse.length; i++) {
                    startProcess(splitResponse[i]);
                }
            }
            try {
            	temp = Integer.parseInt(splitResponse[0]);
            } catch (Exception excpt) {
            	System.err.println("Master Error: Slave response corrupted");
            }
            
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
        this.lock.unlock();
        this.noSlaves = (ML.getSlaves()).isEmpty();
    }
    
    // Starts a new process on a random node
    private void startProcess(String process) {
        SlaveConnection[] slaves = (SlaveConnection[]) (ML.getSlaves()).toArray();
        SlaveConnection target;
        
        target = slaves[(int)(Math.random() * slaves.length)];
        target.messageSlave((this.pid++) + " " + process);
    }
    
    //Migrates process, if it fails to restart it tries again on up to 5 random slaves
    private void migrate(SlaveConnection source, SlaveConnection dest) {
        String sourceResponse;
        String destResponse;
        int tries = 0;
        SlaveConnection[] slaves = null;
        
        sourceResponse = source.messageSlave("PLOP");
        if (sourceResponse.equals("Error")) {
            System.err.println("Master Error: Failed to serialize process");
            return;
        }
        destResponse = dest.messageSlave("PLANT" + sourceResponse);
        while (destResponse.equals("Error")) {
            System.err.println("Master Error: Failed to restart process");
            tries++;
            if (tries >= 30) {
                System.err.println("Master: Giving up on that process, sorry");
                return;
            } else if (tries % 5 == 0) {
                System.err.println("Master: Trying different node");
                if (slaves == null) {
                    slaves = (SlaveConnection[]) (ML.getSlaves()).toArray();
                    
                }
                dest = (SlaveConnection)slaves[(int)(Math.random() * slaves.length)];
            }
            destResponse = dest.messageSlave("PLANT" + sourceResponse);
        }
    }
}