import java.io.IOException;

public class caesarCipher implements MigratableProcess {

	private static final long serialVersionUID = -8437240783838198401L;
	private String nameAndArgs = "caesarCipher";
    private TransactionalFileInputStream in;
    private TransactionalFileOutputStream out;
    private int shift;
    private final String alpha = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private volatile boolean suspended;
    
    public caesarCipher(String[] args) {
        for (String arg : args) {
            this.nameAndArgs += " " + arg;
        }
        
        if ((args.length > 3) || (args.length < 2)) {
            System.out.print("\nUsage: caesarCipher <shift> <inputFile> [outputFile]\n->>");
            return;
        }
        
        try {
            this.shift = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.err.print("\nShift has to be an integer between 0 and 26\n->>");
            return;
        }
        
        if ((this.shift < 0) || (this.shift > 26)) {
            System.err.print("\nShift has to be an integer between 0 and 26\n->>");
            return;
        }
        
        try {
            in = new TransactionalFileInputStream(args[1]);
            if (args.length > 2) {
                out = new TransactionalFileOutputStream(args[2],false);
            } else {
                out = new TransactionalFileOutputStream("encoded" + args[2],false);
            }
        } catch (Exception e) {
            System.err.print("\nCould not open file\n->>");
            return;
        }
    }
    
    public void run() {
        char inputChar;
        char outputChar;
        int index;
        
        while (!suspended) {
            try {
                inputChar = (char) in.read();
            } catch (IOException e) {
            	System.err.println("Read failed!");
            	return;
            }

            if (Character.isLetter(inputChar)) {
            	index = alpha.indexOf(inputChar);
            	outputChar = alpha.charAt(index + this.shift);
            } else {
            	outputChar = inputChar;
            }
            try {
            	out.write(outputChar);
            } catch (IOException e) {
            	System.err.println("Write failed!");
            	return;
            }
        }
        
        this.suspended = false;
        return;
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