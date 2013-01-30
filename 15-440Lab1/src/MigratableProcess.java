import java.io.Serializable;


public interface MigratableProcess extends Runnable, Serializable {

	/* This inference allows processes to utilize our
	   migration methods, they can be totally nomadic like that */
	
	void suspend();
	String toString();
}