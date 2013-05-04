

public class DNA {
    
    public static void main(String[] args) {
        int numberClusters;
		int numberStable;
		Random seed;
		String fileName;
        String lineIn;
        LinkedList<String> strands = new LinkedList<String>();
        LinkedList<CentroidDNA> centroids = new LinkedList<CentroidDNA>();
        
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
        
        
    }
    
    private static void printUsage() {
		System.out.println(" Usage: java DNA [string file] [# clusters] [seed (optional)]");
	}
}