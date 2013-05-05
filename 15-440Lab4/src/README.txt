NOTE: These programs are meant to be run on the GHC machines!

To Compile:
   Use the provided make file to compile the DNA and DataPoints programs:

     make

   This will compile DataPoints, DataPointsMPI, DNA, and DNAMPI

To Run:
   In order to run the serial programs, use the following commands:

     java DataPoints [data file] [# clusters] [seed (optional)]

     java DNA [data file] [# clusters] [seed (optional)]

   Where the data file is the file containing the 2-D data points or DNA strands made using
   the given generators (follow the below instructions for using the DNA strand generator),
   # clusters is the number of centroids (thus, clusters) to be found, and the seed is an
   optional value to ensure the programs always pick the same set of points from the data
   set as starting centroids.

   To run the MPI versions of the programs, the following commands are recommended:

     mpirun -np [# processes] -machinefile [list of nodes] java DataPointsMPI [data file]
     [# clusters] [seed (optional)]

     mpirun -np [# processes] -machinefile [list of nodes] java DNAMPI [data file]
     [# clusters] [seed (optional)]

   Where # processes are the number of instances of the program to be run, list of nodes
   is a file containing a node per line for OpenMPI to use (note that these nodes must be
   reachable without password or athentication from the host node), and the remaining
   parameters are the same as those for the serial version.

To Run DNAGenerator.py
   Use the following command to run the DNA generator:

     python DNAGenerator.py

   In order to change the parameters, open the DNAGenerator.py source code and modify the
   variables on the top of the file. The program generates strands by first generating a 
   number of centroids, and then it changes random bases in the centroids to create unique
   DNA strands. Note that due to the anti-duplicate code, it is possible to have an 
   infinite loop if the length/number of centroids is too small for the number of DNA 
   strands being generated.
