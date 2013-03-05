#
# Makefile to build our (mrrosen and mhansen1) processManager
# 
JVC = javac
JVFLAGS = 

all: rmi_registry rmi_server rmi_client

rmi_registry: RegistryServer.java RMIRegistryServer.java RegistryEntry.java RegistryMessage.java
	$(JVC) $(JVFLAGS) $(LIBS) RegistryServer.java

rmi_server: TestRMIServer.java Remote440.java RemoteObjectRef.java RMIRegistryClient.java RMIProxy.java RMIProxySlave.java RMIProxyHandler.java RMIMessage.java RegistryMessage.java AdvancedTests.java AdvancedTestsImpl.java BasicTests.java BasicTestsImpl.java Shipper.java Shipped.java RMI.java
	$(JVC) $(JVFLAGS) $(LIBS) TestRMIServer.java

rmi_client: TestRMIClient.java Remote440.java RemoteObjectRef.java RMIRegistryClient.java RMIProxy.java RMIProxySlave.java RMIProxyHandler.java RMIMessage.java RegistryMessage.java AdvancedTests.java AdvancedTestsImpl.java BasicTests.java BasicTestsImpl.java Shipper.java Shipped.java RMI.java
	$(JVC) $(JVFLAGS) $(LIBS) TestRMIClient.java

clean:
	rm -f *.class *~