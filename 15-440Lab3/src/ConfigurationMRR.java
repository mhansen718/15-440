
public class ConfigurationMRR<KEYIN, VALIN, KEYOUT, VALOUT> {

	public void map(KEYIN key, VALIN value) {
		/* The default map function, an identity map */
		return;
	}
	public void reduce(KEYIN key, VALIN value) {
		/* The default reduce, an indetity reduce */
		return;
	}
	
}
