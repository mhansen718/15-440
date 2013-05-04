

public class DNA {
    
    public static void main(String[] args) {
        int numberClusters;
		int numberStable;
		Random seed;
		String fileName;
        String lineIn;
        LinkedList<String> strands = new LinkedList<String>();
        LinkedList<CentroidDNA> centroids = new LinkedList<CentroidDNA>();
		RandomAccessFile inFile = null;
        Iterator<String> ptIterator;
		Iterator<CentroidDNA> ctIterator;
        
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
			strands.add(lineIn);
			try {
				lineIn = inFile.readLine();
			} catch (IOException excpt) {
				System.out.println(" Error: problem while reading input file");
				return;
			}
		}
        
        /* From strands, choose n random centroids */
        int i = 0
		while (i < numberClusters) {
			int x = seed.nextInt(strands.size());
			CentroidPoint p = new CentroidPoint((points.get(x)).getX(), (points.get(x)).getY());
			if (!(strands.contains(p))) {
				strands.add(p);
                i++;
			}
		}
        
        numberStable = 0;
		while (numberStable < numberClusters) {
			/* Sequentially find the cluster for each point */
			ptIterator = points.iterator();
			while (ptIterator.hasNext()) {
				String p = ptIterator.next();
				ctIterator = centroids.iterator();

				/* Find the cluster */
				if (ctIterator.hasNext()) {
					CentroidDNA ct = ctIterator.next();
					CentroidDNA ctNearest = ct;
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
				CentroidDNA ct = ctIterator.next();
				if (ct.remean()) {
					numberStable++;
				}
			}
		}
        
        /* Print centroids */
		ctIterator = centroids.iterator();
		while (ctIterator.hasNext()) {
			CentroidDNA ct = ctIterator.next();
			System.out.println(" Centroid: " + ct.output());
		}
    }
    
    private static void printUsage() {
		System.out.println(" Usage: java DNA [string file] [# clusters] [seed (optional)]");
	}
}