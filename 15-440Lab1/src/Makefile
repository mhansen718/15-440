#
# Makefile to build our (mrrosen and mhansen1) processManager
# 
JVC = javac
JVFLAGS = 

all: manager process1 process2

manager: ProcessManager.java TransactionalFileInputStream.java TransactionalFileOutputStream.java MigratableProcess.java userInterface.java MasterManager.java SlaveListener.java MasterListener.java SlaveConnection.java
	$(JVC) $(JVFLAGS) $(LIBS) ProcessManager.java

process1: wavSomething.java  TransactionalFileInputStream.java TransactionalFileOutputStream.java MigratableProcess.java
	$(JVC) $(JVFLAGS) $(LIBS) wavSomething.java

process2: caesarCipher.java  TransactionalFileInputStream.java TransactionalFileOutputStream.java MigratableProcess.java
	$(JVC) $(JVFLAGS) $(LIBS) caesarCipher.java


clean:
	rm -f *.class *~ *.ser