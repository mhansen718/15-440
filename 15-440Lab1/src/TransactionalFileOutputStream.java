import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;


public class TransactionalFileOutputStream extends OutputStream implements Serializable{

	private static final long serialVersionUID = 2488121625537896609L;
	private long filePointer;
	private String fileName;
	
	public TransactionalFileOutputStream(String fileName, boolean append) {
		super();
		this.fileName = fileName;
		if (append) {
			try {
				this.filePointer = (new File(this.fileName)).length();
			} catch (Exception excp) {
				System.out.print("Error Creating TransactionalFileOutputStream: " + excp);
			}
		} else {
			this.filePointer = 0;
		}
	}
	
	@Override
	public void write(int byteWriten) throws IOException {
		
		/* First, open a file for writing
		 * Then, seek to filePointer and write the byte
		 * Finally,  update filePointer and close the file*/
		try {
			RandomAccessFile writeMe = new RandomAccessFile(this.fileName, "rw");
			
			writeMe.seek(this.filePointer);
			
			writeMe.write(byteWriten);
			
			this.filePointer = writeMe.getFilePointer();
			
			writeMe.close();
		} catch (FileNotFoundException excp) {
			System.out.print("File Not Found: " + this.fileName);
		}
	}	
}