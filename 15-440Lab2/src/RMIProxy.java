import java.util.Iterator;
import java.util.Set;


public class RMIProxy implements Runnable {

	private Set<ProxyStore> localObjs;
	
	public void addObject(String name, Object newObj) {
		/* This function simply adds a new object to the set. If it is in the set already, remap */
		Iterator<ProxyStore> iterator;
        
		/* Find any object with the same name */
		iterator = this.localObjs.iterator();
		while (iterator.hasNext()) {
            ProxyStore p = iterator.next();
            
            if (p.name.equals(name)) {
            	p.obj = newObj;
            	return;
            }
		}
		
		/* Add a new ProxyStore as there isnt one yet that has this name */
		ProxyStore p = new ProxyStore();
		p.name = name;
		p.obj = newObj;
		this.localObjs.add(p);
		
		return;
	}
	
	public void run() {
	}
}
