import java.io.Serializable;


public abstract class ConfigurationMRR<MAPIN, REDKEY, REDVAL> implements Serializable {
	
	private static final long serialVersionUID = 3131734494646855375L;
	
	public int recordSize;
    public int start;
    public int end; 
    public String inFile;
    public String outFile;
    
    abstract public MAPIN readRecord(Object record);
    
	abstract public Pair<REDKEY, REDVAL> map(MAPIN mapin);
    
	abstract public REDVAL reduce(REDVAL val1, REDVAL val2);
	
    abstract public Object writeRecord(Pair<REDKEY, REDVAL> record);
}
