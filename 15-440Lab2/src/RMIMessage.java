import java.io.Serializable;
import java.lang.reflect.Method;


public class RMIMessage implements Serializable {

	private static final long serialVersionUID = 2131510874174393140L;
	
	/* TODO: Add whatever needs to go in here */
	public String name;
	public Method method;
	public Object[] args;
	public Object returnValue;
	public Exception exception;
}
