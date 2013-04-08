import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ParticipantMRR {
    
    private int processors;
    private String host;
    private int port;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    
    public void main(String args[]) {
        this.processors = Runtime.getRuntime().availableProcessors();
        
        this.host = args[0];
        
        try {
            this.port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            // Our script somehow failed to give a valid integer port... What!?
            System.exit(-1);
        }
        
        try {
            this.socket = new Socket(this.host,this.port);
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            this.in = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            // Couldn't connect to master, no error because this process was created remotely
            System.exit(-1);
        }
        
        
    }
    
}