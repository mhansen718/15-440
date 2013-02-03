import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;


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
		/* Original Data */
		int fileSize = 0;
		int subChunk1Size = 0;
		int subChunk2Size = 0;
		int numChannels = 0;
		int sampleRate = 0;
		int byteRate = 0;
		int blockAlign = 0;
		int bitsPerSample = 0;
		/* New Data */
		int newSize = 0;
		int newDataSize = 0;
		int[] shiftBuffer = new int[0];
		int frontOfBuffer = 0;
		int backOfBuffer = 0;
		int pulledValue = 0;

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
					/* Do a bunch of  calculations and 
					 * writes to file for header now that we have all relevant data */
					shiftBuffer = new int[((byteRate * delay) / 1000)];
					backOfBuffer = shiftBuffer.length - 1;
					newSize = fileSize + shiftBuffer.length;
					newDataSize = subChunk2Size + shiftBuffer.length;
					writeIntToFile(0x52494646, 4);
					writeIntToFile(newSize, 4);
					writeIntToFile(0x57415645, 4);
					writeIntToFile(0x666D7420, 4);
					writeIntToFile(subChunk1Size, 4);
					writeIntToFile(1, 2);
					writeIntToFile(numChannels, 2);
					writeIntToFile(sampleRate, 4);
					writeIntToFile(byteRate, 4);
					writeIntToFile(blockAlign, 2);
					writeIntToFile(bitsPerSample, 2);
					writeIntToFile(0x64617461, 4);
					writeIntToFile(newDataSize, 4);
					break;
				case "goodStuff":
					/* The body of the file, use previous information to parse and process */
					/* Loop over each byte or 2 bytes samples and process them */
					for (int i = 0; i < (32 / bitsPerSample); i++) {
						pulledValue = byteArrayToInt(bytesRead, (32 / bitsPerSample))[0];
						/* Shift the bytesRead array */
						bytesRead = Arrays.copyOfRange(bytesRead, 
								(i + (bitsPerSample / 8)), bytesRead.length);
						shiftBuffer[backOfBuffer] = (((pulledValue << bitsPerSample) >> 
						bitsPerSample) / 4);
						pulledValue = pulledValue + shiftBuffer[frontOfBuffer];
						writeIntToFile(pulledValue, (bitsPerSample / 8));
						frontOfBuffer = (frontOfBuffer + 1) % shiftBuffer.length;
						backOfBuffer = (backOfBuffer + 1) % shiftBuffer.length;
					}
					break;
				}
			}
		} catch (EOFException excpt) {
			/* Get out when the file is done */
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
	
	private void writeIntToFile(int value, int size) throws IOException {
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
