
public class MostRecentCommonAncestor {

	/* Idea and basic operation credits to (I couldnt think of anything cool :( )
	 * http://stevekrenzel.com/finding-friends-with-mapreduce */
	
	public static void main(String[] args) {
		
		if (args.length != 3) {
			System.out.println("Usage: java MutualFamilyMembers [record file] [start index(record #)] [end index(record #)]");
		}
		
		
		/* Use MapReduce to find a list of mutualfamily members, then simply pick
		 * the most recent using the birthday */
		
		MRRFamilyConfig config = new MRRFamilyConfig();
		
		/* Set up my configuration */
		config.recordSize = 931;
		config.inFile = args[0];
		config.outFile = args[0] + ".out";
		config.start = Integer.parseInt(args[1]);
		config.end = Integer.parseInt(args[2]);
		
		/* Make the job, start it and wait on it! */
		JobMRR myJob = new JobMRR(config, "AncestorJob " + args[1] + "-" + args[2]);
		myJob.submit();
		
		try {
			myJob.waitOnJob();
		} catch (InterruptedException e) {
			System.out.println("We were inturrpted while waiting on our job, :(");
		}
		
		if (myJob.encounteredException()) {
			System.out.println("Oh no!!! The job failed... because a " + myJob.getException().toString() + " Exception happened....");
		} else {
			myJob.readFile();
			
		}
	} 
}
