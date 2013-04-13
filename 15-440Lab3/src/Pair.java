

public class Pair<K,V> implements Serializable {
    
    public K key;
    public V value;
    
    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }
}