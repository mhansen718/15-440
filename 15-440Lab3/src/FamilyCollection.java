import java.util.HashSet;
import java.util.TreeMap;


public class FamilyCollection {

	/* Idea and basic operation credits to (I couldnt think of anything cool :( )
	 * http://stevekrenzel.com/finding-friends-with-mapreduce */

	public static void main(String[] args) {

		if (args.length != 7) {
			System.out.println("Usage: java FamilyCollection [record file] [start index(record #)] [end index(record #)]" + 
					" [family1] [family2] [listen port] [local port]");
			return;
		}


		/* Use MapReduce to find a list of mutualfamily members, then simply pick
		 * the most recent using the birthday */

		MRRFamilyConfig config = new MRRFamilyConfig();

		/* Set up my configuration */
		config.recordSize = 91;
		config.inFile = args[0];
		config.outFile = args[0] + ".out";
		config.start = Integer.parseInt(args[1]);
		config.end = Integer.parseInt(args[2]);
		config.listenBackPort = Integer.parseInt(args[5]);
		config.participantPort = Integer.parseInt(args[6]);
		
		/* Families */
		String family1 = args[3];
		String family2 = args[4];
		
		/* Make the job, start it and wait on it! */
		JobMRR<FamilyRecords, String, HashSet<MemberRecord>> myJob = new JobMRR<FamilyRecords, String, HashSet<MemberRecord>>(config, "AncestorJob " + args[1] + "-" + args[2]);
		myJob.submit();
		
		try {
			myJob.waitOnJob();
		} catch (InterruptedException e) {
			System.out.println("We were inturrpted while waiting on our job, :(");
		}
		
		if (myJob.encounteredException()) {
			System.out.println("Oh no!!! The job failed... because a " + myJob.getException().toString() + " happened....");
		} else {
			TreeMap<String, HashSet<MemberRecord>> reduced = null;
			try {
				reduced = (TreeMap<String, HashSet<MemberRecord>>) myJob.readFile();
			} catch (Exception excpt) {
				System.out.println("Failed to read the product file from reduce");
				return;
			}
			if (reduced.containsKey(family1) && reduced.containsKey(family2)) {
				HashSet<MemberRecord> commonMembers = reduced.get(family1);
				System.out.println("Members " + family1 + ":");
				for (MemberRecord mem : reduced.get(family1)) {
					System.out.println(mem.firstName + " " + mem.lastName + " " + Integer.toString(mem.birthyear));
				}
				System.out.println("Members " + family2 + ":");
				for (MemberRecord mem : reduced.get(family2)) {
					System.out.println(mem.firstName + " " + mem.lastName + " " + Integer.toString(mem.birthyear));
				}
				commonMembers.retainAll(reduced.get(family2));
				if (commonMembers.size() != 0) {
					System.out.println("Common Members");
					for (MemberRecord mem : commonMembers) {
						System.out.println(mem.firstName + " " + mem.lastName + " " + Integer.toString(mem.birthyear));
					}
				} else {
					System.out.println("These families have no common members");
				}
				
				return;
			}
		}
		System.out.println("These two families are not in the records");
		return;
	} 
}
