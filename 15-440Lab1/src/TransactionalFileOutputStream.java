import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;


public class TransactionalFileOutputStream extends FileInputStream implements Serializable{

	private static final long serialVersionUID = 2488121625537896609L;
	private int filePointer;
	
	public TransactionalFileOutputStream(File file)
			throws FileNotFoundException {
		super(file);
		this.filePointer = 0;
	}
	

}