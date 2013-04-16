#
# Makefile to build our (mrrosen and mhansen1) MapReduce Red
# 
JVC = javac
JVFLAGS = 

all: mrr_server mrr_client family_example word_example

mrr_server: ServerMRR.java MasterMRR.java MasterPeonHandlerMRR.java JobEntry.java Peon.java UserInterface.java TaskEntry.java MasterJobHandlerMRR.java PeonListener.java ParticipantStatus.java ConfigurationMRR.java ServerTerminationException.java Pair.java TaskID.java
	$(JVC) $(JVFLAGS) $(LIBS) ServerMRR.java

mrr_client: ClientMRR.java ParticipantMRR.java ParticipantMinionMRR.java JobEntry.java TaskEntry.java TaskID.java ConfigurationMRR.java Pair.java ParticipantStatus.java ParticipantListenerMRR.java 
	$(JVC) $(JVFLAGS) $(LIBS) ClientMRR.java

family_example: FamilyCollection.java MemberRecord.java FamilyRecords.java MRRFamilyConfig.java ConfigurationMRR.java JobMRR.java JobExec.java Pair.java
	$(JVC) $(JVFLAGS) $(LIBS) FamilyCollection.java

word_example: WordCount.java WordCountConfig.java ConfigurationMRR.java JobMRR.java JobExec.java Pair.java
	$(JVC) $(JVFLAGS) $(LIBS) WordCount.java

clean:
	rm -f -- *.class *~ *.mrr