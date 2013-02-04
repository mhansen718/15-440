public class caesarCipher implements MigratableProcess {
    private String nameAndArgs = "caesarCipher";
    private TransactionalFileInputStream in;
    private TransactionalFileOutputStream out;
    private volatile boolean suspended;
    
    public caesarCipher(String[] args) {
        if (args.length > 2) || (args.length == 0) {
            System.out.println("Usage: caesarCipher <inputFile> [outputFile]");
        }
        
        for (String arg : args) {
            this.nameAndArgs += " " + arg;
        }
        
        this.suspended = false;
        return;
    }
    
    public void run() {
        
    }
    
    public void suspend() {
        this.suspended = true;
        
        while (this.suspended);
        return;
    }
    
    public String toString() {
        return this.nameAndArgs;
    }
    
}