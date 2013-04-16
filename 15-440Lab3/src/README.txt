MapReduce Red v1.0

HOW TO COMPILE & RUN:
    In order to run our MapReduce facility on an andrew machine, our Makefile can be used 
    to compile the entire system and examples:

       make

     Once made, our master can be brought online by the following command:

       java ServerMRR [config file]

     Where the config file is the configuration file specified by our System Admin view
     documentation. Should the remote start functionality be turned off (see record for
     detains), each client can be started via the following command:

       java ClientMRR [master host] [master port] [local listen port]

     Where the master host and port are the hostname and port of the Server and the local
     listen port is the port on which this client will listen for new job requests. Note 
     that the master will not accept connections from clients not listed in the config
     file.

     Once the server is running, a prompt should appear and allow the user to interface
     with the MapReduce Red System. See the System Admin view documentation for commands.

HOW TO RUN OUR EXAMPLES:
    Two examples are provide with the code, MostRecentCommonAncestor and . 
    These examples require records specific form and example record sets are provided 
    (family.txt and respectively).

    To run these examples, the following two commands are required:

       java MostRecentCommondAncestor [record file] [start record] [end record] [person1] [p1 birthyear] [person2] [p2 brithyear] [listen port] [local port]

       java

     The first example takes a dataset of family tree information in which each record
     contains a person's name and birthyear as well as there parents, parent's parents, 
     etc. for 5 generations (including themselves). Using this tree, the dataset is
     transformed via MapReduce into a list of paired
