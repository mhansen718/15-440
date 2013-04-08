import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class Basic {

	public static void main(String[] args) throws IOException {
		RandomAccessFile mine = new RandomAccessFile("hi.txt", "rw");
		
		mine.writeInt(13);
	}
}
