
public class ParticipantMinionMRR implements Runnable {
	
	private ParticipantMRR master;
	private int myID;
	
	public ParticipantMinionMRR(ParticipantMRR master, int id) {
		super();
		this.master = master;
		this.myID = id;
	}
	
	public void run() {
		JobEntry currentJob = null;
		
		/* Main loop */
		while (true) {
			/* If Im just taking up space, commit seppuku */
			if (Runtime.getRuntime().availableProcessors() < this.myID) {
				return;
			}
			
			/* Get the next job off the queue, if none, just sit and wait */
			try {
				currentJob = this.master.getNextJob();
			} catch (Exception excpt) {
				/* Failed to get the job, sadly, not much we can do :( */
				return;
			}
			
			// TODO: Do the job, thats your part...
		}
	}

}
