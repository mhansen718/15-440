
public class ServerTerminationException extends Exception {

	private static final long serialVersionUID = -4664615409592581329L;

	/* Indicates that a job was terminated by the terminal */
	public ServerTerminationException() {
		super();
	}
	
	public ServerTerminationException(String msg) {
		super(msg);
	}
}
