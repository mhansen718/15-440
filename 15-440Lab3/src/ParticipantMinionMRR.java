import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;


public class ParticipantMinionMRR implements Runnable {
	
	private ParticipantMRR master;
	private int myID;
	
	public ParticipantMinionMRR(ParticipantMRR master, int id) {
		super();
		this.master = master;
		this.myID = id;
	}
	
	public void run() {
		TaskEntry currentTask = null;
		
		/* Main loop */
		while (true) {
			/* If Im just taking up space, commit seppuku */
			if (Runtime.getRuntime().availableProcessors() < this.myID) {
				return;
			}
			
			/* Get the next job off the queue, if none, just sit and wait */
			try {
				currentTask = this.master.getNextTask();
			} catch (Exception excpt) {
				/* Failed to get the job, sadly, not much we can do :( */
				return;
			}
            
            mapReduce(currentTask.config, currentTask);
            
			/* Add the job to the completed work list */
			master.completeTask(currentTask.id);
		}
	}
    
    private <MAPIN, REDKEY, REDVAL> void mapReduce(ConfigurationMRR<MAPIN, REDKEY, REDVAL> config, TaskEntry currentTask) {
        int nextRecord;
        ArrayList<Pair<REDKEY,REDVAL>> mapOut;
        RandomAccessFile raf;
        FileInputStream in;
        ObjectOutputStream out;
        ObjectInputStream objIn;
        byte[] input;
        TreeMap<REDKEY,ArrayList<REDVAL>> redIn1 = null;
        TreeMap<REDKEY,ArrayList<REDVAL>> redIn2 = null;
        TreeMap<REDKEY,ArrayList<REDVAL>> redOut = null;
        ArrayList<REDVAL> vals;
        Iterator<REDKEY> iter;
        REDKEY key;
        REDVAL val;
        
        System.out.println("file1: " + currentTask.file1);
        System.out.println("file2: " + currentTask.file2 + "\n");
        
        // Do we have to map, or is this just a reduce?
        if (currentTask.file2 != null) {
            nextRecord = currentTask.id.start;
            input = new byte[currentTask.recordSize];
            try {
                raf = new RandomAccessFile(currentTask.file1, "r");
                raf.seek(nextRecord * currentTask.recordSize);
                in = new FileInputStream(raf.getFD());
                redIn1 = new TreeMap<REDKEY, ArrayList<REDVAL>>();
                for (int i = nextRecord; i < currentTask.id.end; i++) {
                    in.read(input);
                    mapOut = config.map(config.readRecord(input));
                    for (Pair<REDKEY,REDVAL> p : mapOut) {
                        vals = redIn1.get(p.key);
                        if (vals == null) {
                            vals = new ArrayList<REDVAL>();
                            vals.add(p.value);
                            redIn1.put(p.key,vals);
                        } else {
                            vals.add(p.value);
                        }
                    }
                }
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            redIn2 = new TreeMap<REDKEY, ArrayList<REDVAL>>();
        } else {
            try {
                objIn = new ObjectInputStream(new FileInputStream(currentTask.file1));
                redIn1 = (TreeMap<REDKEY, ArrayList<REDVAL>>) objIn.readObject();
                objIn.close();
                objIn = new ObjectInputStream(new FileInputStream(currentTask.file2));
                redIn2 = (TreeMap<REDKEY, ArrayList<REDVAL>>) objIn.readObject();
                objIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        redOut = new TreeMap<REDKEY, ArrayList<REDVAL>>();
        iter = (redIn1.keySet()).iterator();
        while (iter.hasNext()) {
            key = iter.next();
            vals = redIn1.get(key);
            
            if (redIn2.containsKey(key)) {
                vals.addAll(redIn2.get(key));
                redIn2.remove(key);
            }
            
            val = vals.get(0);
            for (int i = 1; i < vals.size(); i++) {
                val = config.reduce(val,vals.get(i));
            }
            
            vals = new ArrayList<REDVAL>();
            vals.add(val);
            
            redOut.put(key,vals);
        }
        
        iter = (redIn2.keySet()).iterator();
        while (iter.hasNext()) {
            key = iter.next();
            vals = redIn2.get(key);
            
            val = vals.get(0);
            for (int i = 1; i < vals.size(); i++) {
                val = config.reduce(val,vals.get(i));
            }
            
            vals = new ArrayList<REDVAL>();
            vals.add(val);
            
            redOut.put(key,vals);
        }
        
        try {
            out = new ObjectOutputStream(new FileOutputStream(currentTask.id.toFileName()));
            out.writeObject(redOut);
            out.close();
        } catch (Exception e) {
            // Failed to write output file
        }
    }

}
