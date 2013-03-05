import java.io.Serializable;


public class Shipper implements Serializable {

	private static final long serialVersionUID = 7053676445261661699L;
	
	public String[] cargo = {"Goats", "Cheese", "Rugs", "Gold"};
	
	public String toString() {
		String returnString = "   Cargo: ";
		for (String s : cargo) {
			returnString = returnString + s + ", ";
		}
		return returnString;
	}
}
