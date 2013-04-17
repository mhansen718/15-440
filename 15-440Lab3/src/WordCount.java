import java.util.Iterator;
import java.util.TreeMap;
import java.io.FileOutputStream;


public class WordCount {
    // It's boring and simple, but that makes it great as a reliable test
    
    public static void main(String[] args) {
    
        if (args.length < 5) {
            System.out.println("Usage: java WordCount [inputfile1] [inputfile2] [outputfile] [recordSize1] [recordSize2] [startRecord] [endRecord] [return port] [local port]");
            return;
        }
        
        WordCountConfig config1 = new WordCountConfig();
        WordCountConfig config2 = new WordCountConfig();
        
        try {
            config1.recordSize = Integer.parseInt(args[3]);
            config1.start = Integer.parseInt(args[5]);
            config1.end = Integer.parseInt(args[6]);
            config2.recordSize = Integer.parseInt(args[4]);
            config2.start = Integer.parseInt(args[5]);
            config2.end = Integer.parseInt(args[6]);
            config1.listenBackPort = Integer.parseInt(args[7]);
            config1.participantPort = Integer.parseInt(args[8]);
        } catch (NumberFormatException e) {
            System.out.println("Usage: java WordCount [inputfile1] [inputfile2] [outputfile] [recordSize1] [recordSize2] [startRecord] [endRecord] [return port] [local port]");
            return;
        }
        
        config1.inFile = args[0];
        config1.outFile = args[0] + ".out";
        config2.inFile = args[1];
        config2.outFile = args[1] + ".out";
        
        JobMRR<String,String,Integer> job1 = new JobMRR<String,String,Integer>(config1);
        JobMRR<String,String,Integer> job2 = new JobMRR<String,String,Integer>(config2);
        job1.submit();
        job2.submit();
        
        try {
			job1.waitOnJob();
            job2.waitOnJob();
		} catch (InterruptedException e) {
			System.out.println("We were interrupted while waiting on our job, :(");
		}
        
        // Check for exceptions
        if (job1.encounteredException()) {
            System.out.println("Job failed due to exception: " + (job1.getException()).toString());
            return;
        }
        
        if (job2.encounteredException()) {
            System.out.println("Job failed due to exception: " + (job2.getException()).toString());
            return;
        }
        
        // Display the word frequency of common words
        TreeMap<String, Integer> words1 = null;
        TreeMap<String, Integer> words2 = null;
		try {
			words1 = job1.readFile();
            words2 = job2.readFile();
		} catch (Exception e) {
			System.out.println("Failed to read file :(");
			return;
		}
        
        Iterator<String> iter = (words1.keySet()).iterator();
        String word;
        String allWords = "";
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(args[1]);
        } catch (Exception e) {
            System.err.println("Failed to open file for writing");
        }
        while (iter.hasNext()) {
            word = iter.next();
            if (words2.containsKey(word)) {
                allWords = allWords + word + ": " + args[0] + ": " + words1.get(word) + " " + args[1] + ": " + words2.get(word) + "\n";
            }
        }
        
        try {
            out.write(allWords.getBytes());
        } catch (Exception e) {
            System.err.println("Failed to write to file");
        }
        
        try {
            out.close();
        } catch (Exception e) {
            System.err.println("Failed to close file");
        }
    }
}