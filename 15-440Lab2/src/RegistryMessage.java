import java.io.Serializable;


public class RegistryMessage implements Serializable {

	/* Stuff needed in messages between registry and registry clients */
	public String funct;
	public String objHost;
	public int objPort;
	public String objName;
	public String objInterfaces;
}
