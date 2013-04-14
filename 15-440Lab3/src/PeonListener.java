public class PeonListener implements Runnable {
    int port;
    private ConcurrentLinkedQueue<Peon> peons;
    private ServerSocket listenSocket = null;
    
    public PeonListener(port,peons) {
        this.port = port;
        this.peons = peons;
    }
    
    public void run() {
        try {
            this.listenSocket = new ServerSocket(port);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + this.port);
            System.exit(-1);
        }
        
        while (true) {
            try {
                updatePeon(listenSocket.accept());
            } catch (IOException e) {
                System.err.println("Accept error");
                continue;
            }
        }
    }
    
    private void updatePeon(Socket socket) {
        
    }
}