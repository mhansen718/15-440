
public class ParticipantListenerMRR implements Runnable {

	private ParticipantMRR master;
	private int listenPort;
	
	public ParticipantListenerMRR(ParticipantMRR master, int port) {
		super();
		this.master = master;
		this.listenPort = port;
	}
	
	@Override
	public void run() {
		/* Sit and listen to the rain drops, I mean, the network
		 * on the listen port for new job requests */
		
		while (true) {
		// TODO: Get job from the app (sent by the JobExec) and add it t the queue
			JobEntry newJob;
			
			this.master.addNewJob(newJob);
		}
	}

}
