import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;


public class wavSomething implements MigratableProcess {

	private static final long serialVersionUID = -5986283291309750150L;
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
		/* field:
		 * 1 - RIFF check
		 * 2 - fileSize
		 * 3 - WAVE check
		 * 4 - fmt check
		 * 5 - subchk1Size
		 * 6 - audioFormat check
		 * 7 - sampleRate
		 * 8 - byteRate
		 * 9 - blockAlign
		 * 10 - data check
		 * 11 - subchk2Size
		 * 12 - read and process data
		 */
		String field = new String("RIFF");
		byte[] bytesRead = new byte[4];
		int doneReading = 0;
		int extraBytes = 0;
		int offset = 0;

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
			/* Either the process is suspended or the file s completely written */
			while (!suspendMe && !((doneReading == -1) && (extraBytes >= shiftBuffer.length))) {
				
				/* If we're reading from a 8bit file, add an offset to unused 
				 * elements of bytesRead */
				if ((bitsPerSample <= 8) && (field == "goodStuff")) {
					Arrays.fill(bytesRead, ((byte) 128));
				} else {
					Arrays.fill(bytesRead, ((byte) 0));
				}
					
				if (doneReading >= 0) {
					doneReading = fileIn.read(bytesRead);
				}

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
					shiftBuffer = new int[(((byteRate * delay) / 1000) / (bitsPerSample / 8))];
					Arrays.fill(shiftBuffer, 0);
					backOfBuffer = shiftBuffer.length - 1;
					newSize = fileSize + ((byteRate * delay) / 1000);
					newDataSize = subChunk2Size + ((byteRate * delay) / 1000);
					writeIntToFile(0x46464952, 4);
					writeIntToFile(newSize, 4);
					writeIntToFile(0x45564157, 4);
					writeIntToFile(0x20746D66, 4);
					writeIntToFile(subChunk1Size, 4);
					writeIntToFile(1, 2);
					writeIntToFile(numChannels, 2);
					writeIntToFile(sampleRate, 4);
					writeIntToFile(byteRate, 4);
					writeIntToFile(blockAlign, 2);
					writeIntToFile(bitsPerSample, 2);
					writeIntToFile(0x61746164, 4);
					writeIntToFile(newDataSize, 4);
					fileOut.flush();

					break;
				case "goodStuff":
					/* The body of the file, use previous information to parse and process */
					/* Loop over each byte or 2 bytes samples and process them */
					for (int i = 0; i < (32 / bitsPerSample); i++) {
						pulledValue = byteArrayToInt(bytesRead, (32 / bitsPerSample))[i];
						
						/* If its 8-bit or less, handle as 0-255 unsigned. Else, handle as
						 * two's compliment
						 */
						if (bitsPerSample <= 8) {
							offset = (1 << (bitsPerSample - 1));
							shiftBuffer[backOfBuffer] = (pulledValue - offset) / 2;
							pulledValue = satAddUnsigned(pulledValue, 
									shiftBuffer[frontOfBuffer],
									bitsPerSample);
						} else {
							shiftBuffer[backOfBuffer] = sexInt(pulledValue, bitsPerSample) / 2;
							pulledValue = satAddSigned(sexInt(pulledValue, bitsPerSample), 
									shiftBuffer[frontOfBuffer], 
									bitsPerSample);
						}
						
						writeIntToFile(pulledValue, (bitsPerSample / 8));
						frontOfBuffer = (frontOfBuffer + 1) % shiftBuffer.length;
						backOfBuffer = (backOfBuffer + 1) % shiftBuffer.length;
						if (doneReading == -1) {
							extraBytes++;
						}
					}
					fileOut.flush();
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
			returnValue[index] = returnValue[index] | ((b & 0xFF) << shiftAmount);
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
	
	private int sexInt(int sample, int size) {
		return ((sample << (32 - size)) >> (32 - size));
	}
	
	private int satAddSigned(int a, int b, int size) {
		int value = a + b;
		int maxVal = ((1 << (size - 1)) - 1);
		int minVal = sexInt((1 << (size - 1)), size);
		
		if (value > maxVal) {
			return maxVal;
		} else if (value < minVal) {
			return minVal;
		} else {
			return value;
		}
	}
	
	private int satAddUnsigned(int a, int b, int size) {
		int value = a + b;
		int maxVal = ((1 << size) - 1);
		
		if (value > maxVal) {
			return maxVal;
		} else {
			return value;
		}
	}
}
