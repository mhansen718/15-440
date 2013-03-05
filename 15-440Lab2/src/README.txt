Our RMI Tests:

COMPILING:
  To compile, simply run make using the provied Makefile.

RUNNING THIS RMI:
  To run this RMI system, a serve must be running. After the files are compliled, 
  a new server can be ran with the following command:

    java RegistryServer [port]

  Once this server is running, programs can connect to it using the provided host
  and port from the server printout. For example:

    RMI Registry Server Initialized!
     Registry@unix13.andrew.cmu.edu:24000

  This registry can be connected to using host unix13.andrew.cmu.edu and port 24000.

RUNNING PROVIDED TESTS:
  To run the provided tests, the following to commands can be used:

    java TestRMIServer [host] [port]

    java TestRMIClient [host] [port]

  Where the host and port are those of the registry. Running the TestRMIServer will
  exercise the registry functions and bind two objects which will be used by the 
  client. TestRMIClient will run a few tests using RMIs to use and manipulate objects
  on the server. The tests produce printouts and errors, but not in all cases; the
  code should be consulted to make sure values printed are correct (they were when 
  we ran it...).
