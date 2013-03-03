import java.io.Serializable;
import java.lang.reflect.Method;


public class RMIMessage implements Serializable {

	private static final long serialVersionUID = 2131510874174393140L;
	
	/* Stuff for both send and return */
	public String name;
	public Method method;
	public Object[] args;
	public Object returnValue;
	public Exception exception;
}
