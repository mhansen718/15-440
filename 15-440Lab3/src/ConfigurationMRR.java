import java.io.Serializable;
import java.util.ArrayList;


public abstract class ConfigurationMRR<MAPIN, REDKEY, REDVAL> implements Serializable {
	
	private static final long serialVersionUID = 3131734494646855375L;
	
	public int recordSize;
    public int start;
    public int end; 
    public String inFile;
    public String outFile;
    public int listenBackPort;
    public int participantPort;
    
    abstract public MAPIN readRecord(byte[] record) throws Exception;
    
	abstract public ArrayList<Pair<REDKEY, REDVAL>> map(MAPIN mapin) throws Exception;
    
	abstract public REDVAL reduce(REDVAL val1, REDVAL val2) throws Exception;
}
