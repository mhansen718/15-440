import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;


public class RMIProxyHandler implements InvocationHandler {

	private Object obj;
	
	public RMIProxyHandler(Object obj) {
		super();
		this.obj = obj;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable { 
		return method.invoke(this.obj, args);
	}

}
