import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;


public class TransactionalFileOutputStream extends FileOutputStream implements Serializable{

	private static final long serialVersionUID = 2488121625537896609L;
	private int filePointer;
	
	public TransactionalFileOutputStream(String arg0, boolean arg1)
			throws FileNotFoundException {
		super(arg0, arg1);
		this.filePointer = 0;
	}
	
}