import java.io.Serializable;
import java.util.ArrayList;


public abstract class ConfigurationMRR<MAPIN, REDKEY, REDVAL> implements Serializable {
	
	private static final long serialVersionUID = 3131734494646855375L;
	
	public int recordSize;
    public int start;
    public int end; 
    public String inFile;
    public String outFile;
    
    abstract public MAPIN readRecord(byte[] record);
    
	abstract public ArrayList<Pair<REDKEY, REDVAL>> map(MAPIN mapin);
    
	abstract public REDVAL reduce(REDVAL val1, REDVAL val2);
	
    abstract public byte[] writeRecord(Pair<REDKEY, REDVAL> record);
}
