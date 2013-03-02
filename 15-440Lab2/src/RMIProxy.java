import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


public class RMIProxy implements Runnable {

	private Set<ProxyEntry> localObjs;
	private String myHost;
	private int myPort;
	
	public RMIProxy(String host) {
		super();
		this.localObjs = new HashSet<ProxyEntry>();
		this.myHost = host;
		this.myPort = 27000; /* Starting port */
	}
	
	public void addObject(String name, Object newObj) {
		/* This function simply adds a new object to the set. If it is in the set already, remap */
		Iterator<ProxyEntry> iterator;
        
		/* Find any object with the same name */
		iterator = this.localObjs.iterator();
		while (iterator.hasNext()) {
            ProxyEntry p = iterator.next();
            
            if (p.name.equals(name)) {
            	p.obj = newObj;
            	return;
            }
		}
		
		/* Add a new ProxyStore as there isnt one yet that has this name */
		ProxyEntry p = new ProxyEntry();
		p.name = name;
		p.obj = newObj;
		this.localObjs.add(p);
		
		return;
	}
	
	public void run() {
	}
}
