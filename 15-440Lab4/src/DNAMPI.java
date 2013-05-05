

public class DNAMPI {

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
		LinkedList<String> strands = new LinkedList<String>();
		LinkedList<String> pickingStrands = new LinkedList<String>();;
		LinkedList<CentroidDNA> centroids = new LinkedList<CentroidDNA>();
		RandomAccessFile inFile = null;
		Iterator<String> ptIterator;
		Iterator<CentroidDNA> ctIterator;
		int[] mpiNumberStable = new int[1];
		CentroidDNAList[] mpiCentroids;
		CentroidDNAList[] mpiCentroidsSend;
		
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
		mpiCentroids = new CentroidDNAList[size];
		for (int i = 0; i < mpiCentroids.length; i++) {
			mpiCentroids[i] = new CentroidDNAList();
		}
		mpiCentroidsSend = new CentroidDNAList[1];
		mpiCentroidsSend[0] = new CentroidDNAList();
		
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
		
		/* Open and read in the strands */
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
					strands.add(lineIn);
				}
				if (rank == 0) {
					pickingStrands.add(lineIn);
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
        
        /* From strands, choose n random centroids (if youre the root node, otherwise get them from the root) */
		if (rank == 0) {
			for (int i=0; i < numberClusters;) {
				int x = seed.nextInt(pickingStrands.size());
				CentroidDNA p = new CentroidDNA(pickingStrands.get(x));
				if (!(centroids.contains(p))) {
					centroids.add(p);
					i++;
				}
			}
		}
        
        /* Distribute/Get centroids */
		(mpiCentroidsSend[0]).strands = centroids;
		try {
			MPI.COMM_WORLD.Bcast(mpiCentroidsSend, 0, 1, MPI.OBJECT, 0);
		} catch (MPIException excpt) {
			System.out.println(" Error: problem passing out centroids");
			return;
		}
		centroids = (mpiCentroidsSend[0]).strands;
		
        numberStable = 0;
		while (numberStable < numberClusters) {
			/* Sequentially find the cluster for each strand */
			ptIterator = strands.iterator();
			while (ptIterator.hasNext()) {
				String p = ptIterator.next();
				ctIterator = centroids.iterator();

				/* Find the cluster */
				if (ctIterator.hasNext()) {
					CentroidDNA ct = ctIterator.next();
					CentroidDNA ctNearest = ct;
					double distance = ct.distance(p);
					while (ctIterator.hasNext()) {
						ct = ctIterator.next();
						if (distance > ct.distance(p)) {
							ctNearest = ct;
							distance = ct.distance(p);
						}
					}

					/* Add point to cluster */
					ctNearest.addPoint(p);
				}
			}

			/* Gather all the centroids (send them to the root) */
			(mpiCentroidsSend[0]).strands = centroids;
			try {
				MPI.COMM_WORLD.Gather(mpiCentroidsSend, 0, 1, MPI.OBJECT, 
						mpiCentroids, 0, 1, MPI.OBJECT, 0);
			} catch (MPIException excpt) {
				System.out.println(" Error: problem collecting centroids");
				return;
			}
			
			/* Recompute the centroids (if you're the root, otherwise get new centroids from root)
			 * Note: This is a rather simple operation, and the number of centroids is much less
			 * than the number of strands. Thus, the communication overhead is not worth the benefits
			 * of distributing this part of the work. */
			if (rank == 0) {
				centroids = recombineCentroids(mpiCentroids);
				
				numberStable = 0;
				ctIterator = centroids.iterator();
				while (ctIterator.hasNext()) {
					CentroidDNA ct = ctIterator.next();
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
			(mpiCentroidsSend[0]).strands = centroids;
			try{
				MPI.COMM_WORLD.Bcast(mpiCentroidsSend, 0, 1, MPI.OBJECT, 0);
			} catch (MPIException excpt) {
				System.out.println(" Error: problem passing out centroids");
				return;
			}
			centroids = (mpiCentroidsSend[0]).strands;
		}
		
    }
}