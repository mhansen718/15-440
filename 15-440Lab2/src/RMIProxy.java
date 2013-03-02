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
		ProxyEntry p;
		
		p = this.findObject(name);
		
		if (p != null) {
			/* Remap object for entry */
			p.obj = newObj;
		} else {
			/* Add a new ProxyStore as there isnt one yet that has this name */
			p = new ProxyEntry();
			p.name = name;
			p.obj = newObj;
			this.localObjs.add(p);
		}
		
		return;
	}
	
	public ProxyEntry findObject(String name) {
		/* Finds and returns an entry of an object with the given name. If the object does exist, return null */
		Iterator<ProxyEntry> iterator;
		ProxyEntry p;
        
		/* Find any object with the same name */
		iterator = this.localObjs.iterator();
		while (iterator.hasNext()) {
			p = iterator.next();
            
            if (p.name.equals(name)) {
            	return p;
            }
		}
		
		p = null;
		return p;
	}
	
	public void run() {
		/* TODO: Set up socket and listen */
		
		/* TODO: Unpack rmi and spawn thread to handle it */
	}
}
