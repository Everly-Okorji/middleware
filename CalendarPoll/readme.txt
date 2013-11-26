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
3. On a new command prompt window, navigate to the JORAM_HOME directory and run the admin
by executing:
		ant classic_admin
4. Start the program on the command prompt window as Step 3 by executing:
		ant user
		
		
Things To Note:

- The pre-defined list of names are: alice, tom, everly, zirui. This instantiation can be 
found in the User.java file and edited to include as many names as necessary. Note that the
 administrator would also have to create queues for any newly introduced names.
		