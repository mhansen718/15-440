import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class RemoteObjectDeref extends Proxy {

	private static final long serialVersionUID = 5763167076752693817L;

	protected RemoteObjectDeref(InvocationHandler arg0) {
		super(arg0);
	}

}
