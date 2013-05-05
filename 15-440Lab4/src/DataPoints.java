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
		int numberStable;
		Random seed;
		String fileName;
		String lineIn;
		LinkedList<Point2D.Double> points = new LinkedList<Point2D.Double>();
		LinkedList<CentroidPoint> centroids = new LinkedList<CentroidPoint>();
		RandomAccessFile inFile = null;
		Iterator<Point2D.Double> ptIterator;
		Iterator<CentroidPoint> ctIterator;
		
		/* Get and parse the arguments */
		if ((args.length != 2) && (args.length != 3)) {
			printUsage();
			return;
		}
		
		seed = new Random();
		try {
			fileName = args[0];
			numberClusters = Integer.parseInt(args[1]);
			if (args.length == 3) {
				seed = new Random(Integer.parseInt(args[2]));
			}
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
		for (int i=0; i < numberClusters;) {
			int x = seed.nextInt(points.size());
			CentroidPoint p = new CentroidPoint((points.get(x)).getX(), (points.get(x)).getY());
			if (!(centroids.contains(p))) {
				centroids.add(p);
				i++;
			}
		}
		
		numberStable = 0;
		while (numberStable < numberClusters) {
			/* Sequentially find the cluster for each point */
			ptIterator = points.iterator();
			while (ptIterator.hasNext()) {
				Point2D.Double p = ptIterator.next();
				ctIterator = centroids.iterator();

				/* Find the cluster */
				if (ctIterator.hasNext()) {
					CentroidPoint ct = ctIterator.next();
					CentroidPoint ctNearest = ct;
					double distance = p.distance(ct);
					while (ctIterator.hasNext()) {
						ct = ctIterator.next();
						if (distance > p.distance(ct)) {
							ctNearest = ct;
							distance = p.distance(ct);
						}
					}

					/* Add point to cluster */
					ctNearest.addPoint(p);
				}
			}

			/* Recompute the centroids */
			numberStable = 0;
			ctIterator = centroids.iterator();
			while (ctIterator.hasNext()) {
				CentroidPoint ct = ctIterator.next();
				if (ct.remean()) {
					numberStable++;
				}
			}
		}
		
		/* Print centroids */
		ctIterator = centroids.iterator();
		while (ctIterator.hasNext()) {
			CentroidPoint ct = ctIterator.next();
			System.out.println(" Centroid: " + Double.toString(ct.getX()) + ", " + Double.toString(ct.getY()));
		}
	}
		
	
	private static void printUsage() {
		System.out.println(" Usage: java DataPoints [coord file] [# clusters] [seed (optional)]");
	}
}

