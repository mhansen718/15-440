
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
		
        JobEntry newJob = null;
        private ServerSocket listenSocket = null;
        private Socket socket = null;
        private ObjectInputStream in;
        
        try {
            listenSocket = new ServerSocket(listenPort);
        } catch (IOException e) {
            //Could not listen for jobs
        }
        
		while (true) {
            try {
                socket = listenSocket.accept();
                in = new ObjectInputStream(socket.getInputStream());
                newJob = in.readObject();
            } catch (IOException e) {
                //Accept error
            }
            
			this.master.addNewJob(newJob);
		}
	}

}
