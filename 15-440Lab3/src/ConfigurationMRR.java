import java.io.Serializable;


public abstract class ConfigurationMRR<MAPIN, REDKEY, REDVAL> extends Serializable {
	
    int recordSize, startRecord, endRecord;
    
    String inputFile, outputFile;
    
    MAPIN readRecord(Object record);
    
	Pair<REDKEY, REDVAL> map(MAPIN mapin);
    
	REDVAL reduce(REDVAL val1, REDVAL val2);
	
    Object writeRecord(Pair<REDKEY, REDVAL>);
}
