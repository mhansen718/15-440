
public class ParticipantMinionMRR implements Runnable {
	
	private ParticipantMRR master;
	private int myID;
	
	public ParticipantMinionMRR(ParticipantMRR master, int id) {
		super();
		this.master = master;
		this.myID = id;
	}
	
	public void run() {
        int nextRecord;
        Pair<?,?> mapOut;
        ConfigurationMRR<?,?,?> config;
		TaskEntry currentTask = null;
        RandomAccessFile raf;
        FileInputStream in;
        ObjectOutputStream out;
        byte[] input;
        TreeMap<?,ArrayList<?>> redIn1;
        TreeMap<?,ArrayList<?>> redIn2;
        ArrayList<?> vals;
		
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
			
            config = currentTask.config;
            
            // Do we have to map, or is this just a reduce?
            if (currentTask.file2 != null) {
                nextRecord = currentTask.id.start;
                input = new byte[currentTask.recordSize];
                try {
                    raf = new RandomAccessFile(currentTask.file1, "r");
                    raf.seek(nextRecord * currentTask.recordSize);
                    in = new FileInputStream(raf.getFD);
                    redIn1 = new TreeMap();
                    for (int i = nextRecord; i < currentTask.id.end; i++) {
                        in.read(input);
                        mapOut = config.map(readRecord(input));
                        vals = redIn1.get(mapOut.key);
                        if (vals == null) {
                            vals = new ArrayList();
                            vals.add(mapOut.value);
                            redIn1.put(mapOut.key,vals);
                        } else {
                            vals.add(mapOut.value);
                        }
                    }
                } catch (Exception e) {
                                                                            //TODO: error handling
                }
			}
            
			/* Add the job to the completed work list */
			master.completeTask(currentTask.id);
		}
	}

}
