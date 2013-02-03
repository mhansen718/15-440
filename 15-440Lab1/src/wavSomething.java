import java.io.IOException;


public class wavSomething implements MigratableProcess {

	private TransactionalFileInputStream fileIn;
	private TransactionalFileOutputStream fileOut;
	private int delay;
	
	private volatile boolean suspendMe;
	
	public wavSomething(String[] args) throws Exception {
		/* Take the input file and output file and process them as arguments */
		if (args.length != 3) {
			System.out.println("Usage: wavSomething <inputFile> <outputFile> <delay (ms)>");
		}
		
		fileIn = new TransactionalFileInputStream(args[0]);
		fileOut = new TransactionalFileOutputStream(args[1], false);
		delay = Integer.parseInt(args[2]);
	}
	
	public void run() {
		String field = new String("RIFF");
		byte[] bytesRead = new byte[4];
		int fileSize;
		int subChunk1Size;
		int subChunk2Size;
		int numChannels;
		int sampleRate;
		int byteRate;
		int blockAlign;
		int bitsPerSample;

		try {
			while (!suspendMe) {
				fileIn.read(bytesRead);

				/* field determines what part of the header we're in, or if
				 * we're in the body of the file. In any case, readout the important data
				 * and do whatever processing is need to it. Then write anything needed into
				 * the output file.
				 */
				switch (field) {
				case "RIFF":
					if ((bytesRead[0] != 0x52) || 
							(bytesRead[1] != 0x49) || 
							(bytesRead[2] != 0x46) ||
							(bytesRead[3] != 0x46)) {
						System.out.println("wavSomeithng Error: not a RIFF file");
						return;
					}
					field = "fileSize";
					break;
				case "fileSize":
					fileSize = byteArrayToInt(bytesRead, 1)[0];
					field = "WAVE";
					break;
				case "WAVE":
					if ((bytesRead[0] != 0x57) || 
							(bytesRead[1] != 0x41) || 
							(bytesRead[2] != 0x56) ||
							(bytesRead[3] != 0x45)) {
						System.out.println("wavSomeithng Error: not a WAV file");
						return;
					}
					field = "fmt";
					break;
				case "fmt":
					if ((bytesRead[0] != 0x66) || 
							(bytesRead[1] != 0x6D) || 
							(bytesRead[2] != 0x74) ||
							(bytesRead[3] != 0x20)) {
						System.out.println("wavSomeithng Error: subchunk1 error");
						return;
					}
					field = "subchk1Size";
					break;
				case "subchk1Size":
					subChunk1Size = byteArrayToInt(bytesRead, 1)[0];
					field = "audioFormat";
					break;
				case "audioFormat":
					if (byteArrayToInt(bytesRead, 2)[0] != 1) {
						System.out.println("wavSomeithng Error: not PCM format");
						return;
					}
					numChannels = byteArrayToInt(bytesRead, 2)[1];
					field = "sampleRate";
					break;
				case "sampleRate":
					sampleRate = byteArrayToInt(bytesRead, 1)[0];
					field = "byteRate";
					break;
				case "byteRate":
					byteRate = byteArrayToInt(bytesRead, 1)[0];
					field = "blockAlign";
					break;
				case "blockAlign":
					blockAlign = byteArrayToInt(bytesRead, 2)[0];
					bitsPerSample = byteArrayToInt(bytesRead, 2)[1];
					field = "data";
					break;
				case "data":
					if ((bytesRead[0] != 0x64) || 
							(bytesRead[1] != 0x61) || 
							(bytesRead[2] != 0x74) ||
							(bytesRead[3] != 0x61)) {
						System.out.println("wavSomeithng Error: subchunk2 error");
						return;
					}
					field = "dataSize";
					break;
				case "dataSize":
					subChunk2Size = byteArrayToInt(bytesRead, 1)[0];
					field = "goodStuff";
					break;
				case "goodStuff":
					/* The body of the file, use previous information to parse and process */
					break;

				}
			}
		} catch (IOException excpt) {
			System.out.println("wavSomeithng Error: " + excpt);
		}
		/* Read in the wav and process the header data */
		/* Unassert suspend signal and exit */
		suspendMe = false;
		return;
	}
	
	public void suspend() {
		/* The basic suspend function, nothing to do here but assert
		 * the suspend signal and wait for the run function to exit
		 */
		suspendMe = true;
		
		while (suspendMe);
		return;
	}
	
	/* Utility functions */
	private int[] byteArrayToInt(byte[] bytes, int numOfInts) {
		int[] returnValue = {0, 0, 0, 0};
		int shiftAmount = 0;
		int index = 0;

		for (byte b : bytes) {
			if (shiftAmount == (32 / numOfInts)) {
				index++;
				shiftAmount = 0;
			}
			returnValue[index] = returnValue[index] | (b << shiftAmount);
			shiftAmount = shiftAmount + 8;
		}

		return returnValue;
	}
	
	private void writeIntToFile(int value, int size) throws IOException{
		int shrinkSize = size;
		int shrinkValue = value;
		
		while (shrinkSize > 0) {
			fileOut.write(shrinkValue);
			shrinkValue = shrinkValue >> 8;
			shrinkSize--;
		}
		
		return;
	}
}
