import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SlaveConnection extends Thread {
    private PrintWriter out = null;
    private BufferedReader in = null;
    public Boolean dead = false;
    
    public SlaveConnection(Socket socket) {
    	try {
    		this.out = new PrintWriter(socket.getOutputStream(), true);
    		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    	} catch (IOException e) {
    		System.err.println("Could not connect to slave.");
    		return;
    	}
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
