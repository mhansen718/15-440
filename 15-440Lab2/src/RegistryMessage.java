import java.io.Serializable;


public class RegistryMessage implements Serializable {

	private static final long serialVersionUID = 5080883249882036893L;
	
	/* Stuff needed in messages between registry and registry clients */
    public Exception error;
	public String funct;
	public String objHost;
	public int objPort;
	public String objName;
	public Class<?> objClass;
    public String[] regList;
}
