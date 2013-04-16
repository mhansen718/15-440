import java.util.Iterator;
import java.util.TreeMap;



public class WordCount {
    // It's boring and simple, but that makes it great as a reliable test
    
    public static void main(String[] args) {
    
        if (args.length < 4) {
            System.out.println("Usage: java WordCount [inputfile] [recordSize] [startRecord] [endRecord] [return port] [local port]");
            return;
        }
        
        WordCountConfig config = new WordCountConfig();
        
        try {
            config.recordSize = Integer.parseInt(args[1]);
            config.start = Integer.parseInt(args[2]);
            config.end = Integer.parseInt(args[3]);
            config.listenBackPort = Integer.parseInt(args[4]);
            config.participantPort = Integer.parseInt(args[5]);
        } catch (NumberFormatException e) {
            System.out.println("Usage: java WordCount [inputfile] [recordSize] [startRecord] [endRecord] [return port] [local port]");
            return;
        }
        
        config.inFile = args[0];
        
        JobMRR<String,String,Integer> job = new JobMRR<String,String,Integer>(config);
        job.submit();
        
        try {
			job.waitOnJob();
		} catch (InterruptedException e) {
			System.out.println("We were inturrpted while waiting on our job, :(");
		}
        
        // Check for exceptions
        if (job.encounteredException()) {
            System.out.println("Job failed due to exception: " + (job.getException()).toString());
            return;
        }
        
        // Display the word frequency in alphabetical order
        TreeMap<String, Integer> words = null;
		try {
			words = job.readFile();
		} catch (Exception e) {
			System.out.println("Failed to read file :(");
			return;
		}
        
        Iterator<String> iter = (words.navigableKeySet()).iterator();
        String word;
        
        while (iter.hasNext()) {
            word = iter.next();
            System.out.println(word + ": " + words.get(word));
        }
    }
}