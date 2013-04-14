import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;


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
        ServerSocket listenSocket = null;
        Socket socket = null;
        ObjectInputStream in;
        
        try {
            listenSocket = new ServerSocket(listenPort);
        } catch (IOException e) {
            System.out.println("Could not listen on port: " + listenPort);
        }
        
		while (true) {
            try {
                socket = listenSocket.accept();
            } catch (IOException e) {
                System.out.println("Failed to accept connection");
                continue;
            }
            
            try {
                in = new ObjectInputStream(socket.getInputStream());
                newJob = (JobEntry) in.readObject();
            } catch (Exception e) {
                System.out.println("Failed to receive new job");
                continue;
            }
            
            try {
                in.close();
                socket.close();
            } catch (IOException e) {
                System.out.println("Failed to close connection");
            }
            
			this.master.addNewJob(newJob);
		}
	}

}
