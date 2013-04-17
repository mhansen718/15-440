import java.util.Iterator;
import java.util.TreeMap;
import java.io.FileOutputStream;


public class WordCount {
    // It's boring and simple, but that makes it great as a reliable test
    
    public static void main(String[] args) {
    
        if (args.length < 5) {
            System.out.println("Usage: java WordCount [inputfile] [outputfile] [recordSize] [startRecord] [endRecord] [return port] [local port]");
            return;
        }
        
        WordCountConfig config = new WordCountConfig();
        
        try {
            config.recordSize = Integer.parseInt(args[2]);
            config.start = Integer.parseInt(args[3]);
            config.end = Integer.parseInt(args[4]);
            config.listenBackPort = Integer.parseInt(args[5]);
            config.participantPort = Integer.parseInt(args[6]);
        } catch (NumberFormatException e) {
            System.out.println("Usage: java WordCount [inputfile] [outputfile] [recordSize] [startRecord] [endRecord] [return port] [local port]");
            return;
        }
        
        config.inFile = args[0];
        config.outFile = args[0] + ".out";
        
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
        String allWords = "";
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(args[1]);
        } catch (Exception e) {
            System.err.println("Failed to write to output");
        }
        while (iter.hasNext()) {
            word = iter.next();
            allWords = allWords + word + ": " + words.get(word) + "\n";
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