

public class WordCountConfig extends ConfigurationMRR<String,String,int> {
    
    
    public String readRecord(byte[] record) {
        return (new String(record)).trim();
    }
    
    public ArrayList<Pair<String,int>> map(String word) {
        ArrayList<Pair<String,int>> val = new ArrayList();
        val.add(new Pair(word,1));
        return val;
    }
    
    public int reduce(int val1, int val2) {
        return val1 + val2;
    }
}