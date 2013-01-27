
public class ProcessManager {
    
    private final String hostname;
    
    private void ProcessManager(String h) {
        if (!h) {
            masterManager();
        } else {
            this.hostname = h;
            slaveManager();
        }
    }
    
    private void masterManager() {
        
    }
    
    private void slaveManager() {
        
    }
    
	public static void main(String[] args) {
        private String h;
        private int i = 0;
        while (i < args.length) {
            if (args[i] == "-c") {
                i++;
                h = args[i];
                i++;
            } else {
                return "Invalid argument";
            }
        }
        ProcessManager p = new ProcessManager(h);
	}
}
