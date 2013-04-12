import java.io.Serializable;


public class ConfigurationMRR<KEYIN, VALIN, KEYOUT, VALOUT> implements Serializable {

	private static final long serialVersionUID = 573305004071782408L;
	
	public void map(KEYIN key, VALIN value) {
		/* The default map function, an identity map */
		return;
	}
	public void reduce(KEYIN key, VALIN value) {
		/* The default reduce, an indetity reduce */
		return;
	}
	
}
