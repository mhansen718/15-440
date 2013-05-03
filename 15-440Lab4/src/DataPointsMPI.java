import java.awt.geom.Point2D;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import mpi.*;

// Usage: mpirun -np 1 -machinefile list2.mpi java DataPointsMPI scripts/input/cluster.csv 2
public class DataPointsMPI {

	public static void main (String[] args) {
		int numberClusters;
		int numberStable;
		int lines;
		int currentLine;
		int rank;
		int size;
		Random seed;
		String fileName;
		String lineIn;
		LinkedList<Point2D.Double> points = new LinkedList<Point2D.Double>();
		LinkedList<Point2D.Double> pickingPoints = new LinkedList<Point2D.Double>();;
		LinkedList<CentroidPoint> centroids = new LinkedList<CentroidPoint>();
		RandomAccessFile inFile = null;
		Iterator<Point2D.Double> ptIterator;
		Iterator<CentroidPoint> ctIterator;
		int[] mpiNumberStable = new int[1];
		CentroidPointList[] mpiCentroids;
		CentroidPointList[] mpiCentroidsSend;
		
		/* Initialize the MPI environment */
		try {
			MPI.Init(args);
			rank = MPI.COMM_WORLD.Rank();
			size = MPI.COMM_WORLD.Size();
		} catch (MPIException excpt) {
			System.out.println(" Error: failed to initialize mpi environment");
			return;
		}
		
		/* Initialize the buffers */
		mpiCentroids = new CentroidPointList[size];
		for (int i = 0; i < mpiCentroids.length; i++) {
			mpiCentroids[i] = new CentroidPointList();
		}
		mpiCentroidsSend = new CentroidPointList[1];
		mpiCentroidsSend[0] = new CentroidPointList();
		
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
		lines = 0;
		try {
			inFile = new RandomAccessFile(fileName, "r");
			/* Find the number of lines in the file */
			while (inFile.readLine() != null) {
				lines++;
			}
			inFile.seek(0);
		} catch (FileNotFoundException excpt) {
			System.out.println(" Error: file " + fileName + " not found");
			return;
		} catch (IOException excpt) {
			System.out.println(" Error: problem while reading input file");
			return;
		}
		
		try {
			lineIn = inFile.readLine();
		} catch (IOException excpt) {
			System.out.println(" Error: problem while reading input file");
			return;
		}
		
		/* Get Range based on rank */
		int mod = lines % size;
		int myMaxLine = (lines / size) * (rank + 1);
		int myMinLine = (lines / size) * rank;
		if (rank >= mod) {
			myMaxLine += mod;
			myMinLine += mod;
		} else {
			myMaxLine += rank + 1;
			myMinLine += rank;
		}
		currentLine = 0;
		while (lineIn != null) {
			try {
				if ((currentLine >= myMinLine) && (currentLine < myMaxLine)) {
					Point2D.Double p = new Point2D.Double();
					p.setLocation(Double.parseDouble(lineIn.split(",")[0]), Double.parseDouble(lineIn.split(",")[1]));
					points.add(p);
				}
				if (rank == 0) {
					Point2D.Double p = new Point2D.Double();
					p.setLocation(Double.parseDouble(lineIn.split(",")[0]), Double.parseDouble(lineIn.split(",")[1]));
					pickingPoints.add(p);
				}
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
			currentLine++;
		}
		
		/* From points, choose n random centroids (if youre the root node, otherwise get them from the root) */
		if (rank == 0) {
			for (int i=0; i < numberClusters; i++) {
				int x = seed.nextInt(pickingPoints.size());
				CentroidPoint p = new CentroidPoint((pickingPoints.get(x)).getX(), (pickingPoints.get(x)).getY());
				if (!(centroids.contains(p))) {
					centroids.add(p);
				}
			}
		}
		
		/* Distribute/Get centroids */
		(mpiCentroidsSend[0]).points = centroids;
		try {
			MPI.COMM_WORLD.Bcast(mpiCentroidsSend, 0, 1, MPI.OBJECT, 0);
		} catch (MPIException excpt) {
			System.out.println(" Error: problem passing out centroids");
			return;
		}
		centroids = (mpiCentroidsSend[0]).points;
		
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

			/* Gather all the centroids (send them to the root) */
			(mpiCentroidsSend[0]).points = centroids;
			try {
				MPI.COMM_WORLD.Gather(mpiCentroidsSend, 0, 1, MPI.OBJECT, 
						mpiCentroids, 0, 1, MPI.OBJECT, 0);
			} catch (MPIException excpt) {
				System.out.println(" Error: problem collecting centroids");
				return;
			}
			
			/* Recompute the centroids (if your the root, otherwise get new centroids from root)
			 * Note: This is a rather simple operation, and the number of centroids is much less
			 * than the number of points. Thus, the communication overhead is not worth the benefits
			 * of distributing this part of the work. */
			if (rank == 0) {
				centroids = recombineCentroids(mpiCentroids);
				
				numberStable = 0;
				ctIterator = centroids.iterator();
				while (ctIterator.hasNext()) {
					CentroidPoint ct = ctIterator.next();
					if (ct.remean()) {
						numberStable++;
					}
				}
			}
			
			/* Distribute/Get numberStable */
			mpiNumberStable[0] = numberStable;
			try {
				MPI.COMM_WORLD.Bcast(mpiNumberStable, 0, 1, MPI.INT, 0);
			} catch (MPIException excpt) {
				System.out.println(" Error: problem passing out centroids");
				return;
			}
			numberStable = mpiNumberStable[0];
			
			/* Distribute/Get centroids */
			(mpiCentroidsSend[0]).points = centroids;
			try{
				MPI.COMM_WORLD.Bcast(mpiCentroidsSend, 0, 1, MPI.OBJECT, 0);
			} catch (MPIException excpt) {
				System.out.println(" Error: problem passing out centroids");
				return;
			}
			centroids = (mpiCentroidsSend[0]).points;
		}
		
		/* Print centroids */
		if (rank == 0) {
			ctIterator = centroids.iterator();
			while (ctIterator.hasNext()) {
				CentroidPoint ct = ctIterator.next();
				System.out.println(" Centroid: " + Double.toString(ct.getX()) + ", " + Double.toString(ct.getY()));
			}
		}
		
		/* Terminate the MPI environment */
		try {
			MPI.Finalize();
		} catch (MPIException excpt) {
			System.out.println(" Error: failed to close mpi environment");
			return;
		}
	}
		
	
	private static void printUsage() {
		System.out.println(" Usage: java DataPoint [coord file] [# clusters] [seed (optional)]");
	}
	
	private static LinkedList<CentroidPoint> recombineCentroids(CentroidPointList[] centroidPoints) {
		LinkedList<CentroidPoint> newCentroids = new LinkedList<CentroidPoint>();
		for (int i = 0; i < centroidPoints.length; i++) {
			LinkedList<CentroidPoint> ctPs = (centroidPoints[i]).points;
			for (CentroidPoint ct : ctPs) {
				if (newCentroids.contains(ct)) {
					newCentroids.get(newCentroids.indexOf(ct)).combine(ct);
				} else {
					newCentroids.add(ct);
				}
			}
		}
		return newCentroids;
	}
}

