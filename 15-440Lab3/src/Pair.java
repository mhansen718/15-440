import java.io.Serializable;



public class Pair<K,V> implements Serializable {
    
	private static final long serialVersionUID = 5509960819469075625L;
	
	public K key;
    public V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}