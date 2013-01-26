import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;


public class TransactionalFileInputStream extends FileInputStream implements Serializable{

	private static final long serialVersionUID = -7459982254823335052L;
	private int filePointer;
	
	public TransactionalFileInputStream(File arg0) throws FileNotFoundException {
		super(arg0);
		this.filePointer = 0;
	}

}
