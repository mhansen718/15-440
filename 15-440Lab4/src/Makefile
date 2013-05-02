#
# Makefile to build our (mrrosen and mhansen1) MapReduce Red
# 
JVC = javac
JVFLAGS = 

all: datapoints datapoints_mpi

datapoints: DataPoints.java CentroidPoint.java
	$(JVC) $(JVFLAGS) $(LIBS) DataPoints.java

datapoints_mpi: DataPointsMPI.java CentroidPoint.java CentroidPointList.java
	$(JVC) $(JVFLAGS) $(LIBS) -cp /usr/local/lib/openmpi/lib/mpi.jar DataPointsMPI.java CentroidPoint.java CentroidPointList.java

clean:
	rm -f *.class *~