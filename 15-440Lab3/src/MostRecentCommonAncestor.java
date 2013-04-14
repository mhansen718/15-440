import java.io.IOException;
import java.util.HashSet;
import java.util.TreeMap;


public class MostRecentCommonAncestor {

	/* Idea and basic operation credits to (I couldnt think of anything cool :( )
	 * http://stevekrenzel.com/finding-friends-with-mapreduce */
	
	public static void main(String[] args) {
		
		if (args.length != 7) {
			System.out.println("Usage: java MutualFamilyMembers [record file] [start index(record #)] [end index(record #)] [person1] [person1 birthyear] [person2] [person2 birthyear]");
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
		
		/* Set up the records for the people to find */
		MemberRecord p1 = new MemberRecord();
		p1.self = args[3];
		p1.birthyear = Integer.parseInt(args[4]);
		MemberRecord p2 = new MemberRecord();
		p2.self = args[5];
		p2.birthyear = Integer.parseInt(args[6]);
		HashSet<MemberRecord> pairing = new HashSet<MemberRecord>();
		pairing.add(p1);
		pairing.add(p2);
		
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
			TreeMap<HashSet<MemberRecord>, HashSet<MemberRecord>> reduced = null;
			try {
				reduced = (TreeMap<HashSet<MemberRecord>, HashSet<MemberRecord>>) myJob.readFile();
			} catch (Exception excpt) {
				System.out.println("Failed to read the product file from reduce");
				return;
			}
			if (reduced.containsKey(pairing)) {
				HashSet<MemberRecord> pair = reduced.get(pairing);
				/* Find the youngest of the common ancestors */
				MemberRecord currentYoung = (MemberRecord) pair.toArray()[0];
				for (MemberRecord mem : pair) {
					if (mem.birthyear > currentYoung.birthyear) {
						currentYoung = mem;
					}
				}
				System.out.println(p1.self + " and " + p2.self + " have " + currentYoung.self + " born " + currentYoung.birthyear + " as their youngest common ancestor");
				return;
			}
		}
		System.out.println("These two people have no common ancestors");
	} 
}
