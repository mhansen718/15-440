public class PeonListener implements Runnable {
    int port;
    private ConcurrentLinkedQueue<Peon> peons;
    private ServerSocket listenSocket = null;
    
    public PeonListener(int port, ConcurrentLinkedQueue<Peon> peons) {
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
        Iterator<Peon> iter = peons.iterator();
        String host = (socket.getInetAddress()).getHostName();
        int port = socket.getPort();
        Peon peon;
        
        while (iter.hasNext()) {
            peon = iter.next();
            if ((host.equals(peon.host)) && (port == peon.port)) {
                peon.socket = socket;
                return;
            }
        }
        System.err.println("Illegal connection attempt from " + host);
        socket.close();
    }
}