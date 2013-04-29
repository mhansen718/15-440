import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.LinkedList;


public class DataPoints {

	public static void main (String[] args) {
		int numberClusters;
		String fileName;
		String lineIn;
		LinkedList<Point> points = new LinkedList<Point>();
		RandomAccessFile inFile = null;
		
		/* Get and parse the arguments */
		if (args.length != 2) {
			printUsage();
			return;
		}
		
		try {
			fileName = args[0];
			numberClusters = Integer.parseInt(args[1]);
		} catch (NumberFormatException excpt) {
			printUsage();
			return;
		}
		
		/* Open and read in the points */
		try {
			inFile = new RandomAccessFile(fileName, "r");
		} catch (FileNotFoundException excpt) {
			System.out.println(" Error: file " + fileName + " not found");
			return;
		}
		
		try {
			lineIn = inFile.readLine();
		} catch (IOException excpt) {
			System.out.println(" Error: problem while reading input file");
			return;
		}
		
		while (lineIn != null) {
			try {
				Point p = new Point();
				p.setLocation(Double.parseDouble(lineIn.split(",")[0]), Double.parseDouble(lineIn.split(",")[1]));
				points.add(p);
			} catch (NumberFormatException excpt) {
				System.out.println(" Error: failed to parse input file");
				return;
			} catch (ArrayIndexOutOfBoundsException excpt) {
				System.out.println(" Error: failed to parse input file");
				return;
			}
			try {
				lineIn = inFile.readLine();
			} catch (IOException excpt) {
				System.out.println(" Error: problem while reading input file");
				return;
			}
		}
	}
	
	private static void printUsage() {
		System.out.println(" Usage: java DataPoint [coord file] [# clusters]");
	}
}
