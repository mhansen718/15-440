import java.util.ArrayList;



public class WordCountConfig extends ConfigurationMRR<String,String,Integer> {
    
	private static final long serialVersionUID = -7356146614937391782L;

	public String readRecord(byte[] record) {
        return (new String(record)).trim();
    }

	@Override
	public ArrayList<Pair<String, Integer>> map(String mapin) {
		 ArrayList<Pair<String,Integer>> val = new ArrayList<Pair<String,Integer>>();
	     val.add(new Pair<String, Integer>(mapin,1));
	     return val;
	}

	@Override
	public Integer reduce(Integer val1, Integer val2) {
		return val1 + val2;
	}
}