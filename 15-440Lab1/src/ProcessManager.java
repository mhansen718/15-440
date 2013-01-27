import java.util.Scanner;

public class ProcessManager {
    
    private ThreadGroup processes;
    private final String hostname;
    
    private void ProcessManager(String h) {
        if (!h) {
            masterManager();
        } else {
            this.hostname = h;
            slaveManager();
        }
    }
    
    // I'll make this two threads, one that manages processes, one that is a slave
    private void masterManager() {
        
    }
    
    private void slaveManager() {
        //TODO: Connect to master
        
        processes = new ThreadGroup("processes");
        Scanner sc = new Scanner(System.in);
        Thread[] threads;
        while (true) {
            System.out.print(">> ");
            String input = sc.nextLine();
            if (input == "ps") {
                processes.enumerate(threads);
                for (Thread t: threads) {
                    System.out.println(t.toString());
                }
            } else if (input == "quit") {
                System.out.println("Goodbye!");
                return;
            } else {
                
            }
        }
    }
    
    public int runningProcesses() {
        return proceses.activeCount();
    }
    
	public static void main(String[] args) {
        String h;
        int i = 0;
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
