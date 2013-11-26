HOW TO RUN THE CALENDAR POLL CLIENT

Before you begin:
- You should have Joram set up on your system.
- You have Apache Ant installed correctly.

Setup:
- Copy the .java source files from the /classic folder to the JORAM_HOME/classic folder.
 Note that the ClassicAdmin file is included here.
- Copy the build.xml file from the parent folder to the JORAM_HOME folder.

Running a Client:
1. Open a command prompt window, navigate to the JORAM_HOME directory and execute:
		ant clean compile
2. On the same command prompt window, execute:
		ant reset single_server
	* Leave this running for the duration of your session.
3. On a new command prompt window, navigate to the JORAM_HOME directory and start the 
program by executing:
		ant user
