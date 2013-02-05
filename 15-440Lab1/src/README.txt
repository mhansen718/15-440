Lab 1 by Michael Hansen and Michael Rosen

ProcessManager runs by java ProcessManager [-c hostname] (for slave) [-p port#] (optional port#)

Our migratable processes are wavSomething and caesarCipher, wavSomething adds an echo effect to wav files, and caesarCipher encodes a text file with a caesar cipher

Usage:
wavSomething <inputFile> <outputFile> <delay (ms)>
caesarCipher <shift> <inputFile> <outputFile> (optional, if not supplied defaults to "encoded_" + inputFile)

The Examples folder holds two simple wav files for use with wavSomething (delays of more than a second (1000) take a very long time to run)