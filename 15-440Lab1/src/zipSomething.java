public class zipSomething implements MigratableProcess {
    
    private static final long serialVersionUID = 58197321546812316L;
    private final String nameAndArgs;
    private TransactionalFileInputStream in;
    private ZipOutputStream out;
    private ZipEntry target;
    private int zipOffset;
    private byte[] buf = new byte[10];
    private volatile boolean suspended;
    
    public void zipSomething(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: zipSomething <inputFile> <outputFile>");
            System.exit(-1);
        }
        
        try {
            in = new TransactionalFileInputStream(args[0]);
            out = new ZipOutputStream(new TransactionalFileOutputStream(args[1],false));
            target = new ZipEntry(args[1]);
            out.putNextEntry(target);
        } catch (IOException e) {
            System.err.println("Could not access files");
            System.exit(-1);
        }
    }
    
    public void run() {
        byte input;
        
        while (!suspended) {
            // Transfer 10 bytes at a time
            for (int i = 0; i < 10; i++) {
                if ((input = in.read()) != null) {
                    buf[i] = input;
                } else {
                    out.write(buf,0,i);
                    out.close();
                    return;
                }
            }
            out.write(buf);
        }
        
        suspended = false;
        return;
    }
    
    @override
    public void suspend() {
        this.suspended = true;
        
        while (suspended);
        return;
    }
    
    @override
    public String toString() {
        return this.nameAndArgs;
    }
}