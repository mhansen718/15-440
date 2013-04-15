

public class WordCount {
    // It's boring and simple, but that makes it great as a reliable test
    
    public static void main(String[] args) {
    
        if (args.length < 4) {
            System.out.println("Usage: java WordCount [inputfile] [recordSize] [startRecord] [endRecord]");
            return;
        }
        
        WordCountConfig config = new WordCountConfig();
        
        try {
            config.recordSize = Integer.parseInt(args[1]);
            config.start = Integer.parseInt(args[2]);
            config.end = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            System.out.println("Usage: java WordCount [inputfile] [recordSize] [startRecord] [endRecord]");
            return;
        }
        
        config.inFile = args[0];
        
        JobMRR<String,String,int> job = new JobMRR<String,String,int>(config);
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
        TreeMap<String,int> words = job.readFile();
        
        Iterator<String> iter = (words.navigableKeySet()).iterator();
        String word;
        
        while (iter.hasNext()) {
            word = iter.next();
            System.out.println(word + ": " + words.get(word));
        }
    }
}