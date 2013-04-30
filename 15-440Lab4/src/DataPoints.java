import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;


public class DataPoints {

	public static void main (String[] args) {
		int numberClusters;
		String fileName;
		String lineIn;
		LinkedList<Point2D.Double> points = new LinkedList<Point2D.Double>();
		LinkedList<CentroidPoint> centroids = new LinkedList<CentroidPoint>();
		RandomAccessFile inFile = null;
		Iterator<Point2D.Double> ptIterator;
		Iterator<CentroidPoint> ctIterator;
		
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
				Point2D.Double p = new Point2D.Double();
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
		
		/* From points, choose n random centroids */
		for (int i=0; i < numberClusters; i++) {
			int x = (new Random()).nextInt(points.size());
			CentroidPoint p = new CentroidPoint((points.get(x)).getX(), (points.get(x)).getY());
			if (!(centroids.contains(p))) {
				centroids.add(p);
			}
		}
		
		/* Sequentially find the cluster for each point */
		ptIterator = points.iterator();
		while (ptIterator.hasNext()) {
			Point2D.Double p = ptIterator.next();
			ctIterator = centroids.iterator();
			
			if (ctIterator.hasNext()) {
				CentroidPoint ct = ctIterator.next();
				CentroidPoint ctNearest = ct;
				double distance = p.distance(ct);
				while (ctIterator.hasNext()) {
					ct = ctIterator.next();
					if (distance < p.distance(ct)) {
						ctNearest = ct;
						distance = p.distance(ct);
					}
				}
			}
			
		}
	}
	
	private static void printUsage() {
		System.out.println(" Usage: java DataPoint [coord file] [# clusters]");
	}
}

