import java.io.Serializable;


public class RMIMessage implements Serializable {

	private static final long serialVersionUID = 2131510874174393140L;
	
	/* Stuff for both send and return */
	public String name;
	public String methodName;
	public Class<?>[] parameterTypes;
	public Object[] args;
	public Object returnValue;
	public Exception exception;
}
