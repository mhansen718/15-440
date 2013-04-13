import java.io.Serializable;


public class ConfigurationMRR<MAPIN, REDKEY, REDVAL> implements Serializable {

	private static final long serialVersionUID = 573305004071782408L;
	
    public MAPIN readRecord(byte[] record) {
        
    }
    
	public Pair<REDKEY, REDVAL> map(MAPIN mapin) {
		/* The default map function, an identity map */
		return new Pair(null, mapin);
	}
    
	public REDVAL reduce(REDVAL val1, REDVAL val2) {
		/* The default reduce, an identity reduce */
		return new Pair(val1,val2);
	}
	
}
