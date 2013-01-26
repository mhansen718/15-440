import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;


public class TransactionalFileInputStream extends InputStream implements Serializable {

	private static final long serialVersionUID = -7459982254823335052L;
	private long filePointer;
	private String fileName;
	
	public TransactionalFileInputStream(String fileName) {
		super();
		this.filePointer = 0;
		this.fileName = fileName;
	}

	@Override
	public int read() throws IOException {
		int byteRead;
		
		/* First, open a file for reading
		 * Then, seek to filePointer and read a byte
		 * Finally,  update filePointer and close the file*/
		try {
			RandomAccessFile readMe = new RandomAccessFile(this.fileName, "r");
			
			readMe.seek(this.filePointer);
			
			byteRead = readMe.read();
			
			this.filePointer = readMe.getFilePointer();
			
			readMe.close();
		}
		catch (FileNotFoundException excp) {
			System.out.print("File Not Found: " + this.fileName);
			byteRead = -1;
		}
		
		return byteRead;
	}
	
	
}
