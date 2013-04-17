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
    (short_family.txt and respectively).

    To run these examples, the following two commands are required:

       java FamilyCollection [record file] [start record] [end record] [family1] [person2] [listen port] [local port]

       java WordCount [inputfile1] [inputfile2] [outputfile] [recordSize1] [recordSize2] [startRecord] [endRecord] [return port1] [return port2] [local port]

     The first example takes a dataset of family tree information in which each record
     contains a person's name and birthyear as well as their parents. Using this small,
     record, the MapReduce facility combines these records into families based on last 
     name (a small unit; ie person and their parents) are placed into both the mother 
     and father's family. Once done, the data is now output as a pair of family names
     (string) and members (hashset). The intersection of two of these records represents
     the common members of both families.

     The second example is a utility that runs a wordcount on two input files and outputs
     the frequency of all the words in the record range that were in both inputs
     Sample input sets include the king james bible (bibleOut.txt, recordSize = 22) and
     the complete works of Shakespear (shakespeareOut.txt, recordSize = 37)
