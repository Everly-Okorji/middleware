package superbroker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class SuperBroker {
	public static void main(String[] args) throws IOException {
		
		//The structure table of the broker-client network
		//                port  parent    children   [A 0 means no such port]
		int[][] ports = {{2001,    0, 2002, 2003,    0},
						 {2002, 2001, 2004, 2005, 2010},
						 {2003, 2001, 2006, 2017, 2018},
						 {2004, 2002, 2011, 2012,    0},
						 {2005, 2002, 2013, 2014,    0},
						 {2006, 2003, 2015, 2016,    0},
						 {2010, 2002,    0,    0,    0},
						 {2011, 2004,    0,    0,    0},
						 {2012, 2004,    0,    0,    0},
						 {2013, 2005,    0,    0,    0},
						 {2014, 2005,    0,    0,    0},
						 {2015, 2006,    0,    0,    0},
						 {2016, 2006,    0,    0,    0},
						 {2017, 2003,    0,    0,    0},
						 {2018, 2003,    0,    0,    0}};
		
		
		
		int portForBrokers = 2001;	//Keep track of current next available port number for Broker
		int portForClients = 2010;	//Keep track of current next available port number for Client
		int portOfSuperBroker = 2000; //The port number for the Super Broker
		int currentPortForBroker=portForBrokers;
		int currentPortForClient=portForClients;
		String inputLine;
		
		try (ServerSocket serverSocket = new ServerSocket(portOfSuperBroker)){ //initialize the ServerSocket
			while (true){
				//Create a new connection with either a Broker or a Client
				Socket clientSocket = serverSocket.accept();
				
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
	            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				
	            inputLine = in.readLine();
	            
	            int childIndex = 2;	// The childIndex starts from 2 according to the structure table
	            while (!inputLine.equals("I'm done.")){
	            	if (inputLine.equals("I'm a Broker. What is my port?")){
	            		//Check if the port number for Broker beyond its range
	            		if (portForBrokers > 2006){
							System.err.println("SuperBroker: There's no more port availble for Broker!");
							System.exit(1);
						}
	            		out.println(portForBrokers);
						currentPortForBroker=portForBrokers;
	            		portForBrokers++;
						childIndex=2;
					}
					else if (inputLine.equals("I'm a Broker. What is my parent's port?")){
						//To find the corresponding entry in the structure table
						for (int i=0; i<15; i++){
							if (ports[i][0] == (currentPortForBroker)){
								out.println(ports[i][1]);
								break;
							}
						}
					}
					else if (inputLine.equals("I'm a Broker. What is my child's port?")){
						//To find the corresponding entry in the structure table
						for (int i=0; i<15; i++){
							if (ports[i][0] == (currentPortForBroker)){
								if (ports[i][childIndex] != 0){
									if (childIndex > 4){
										System.err.println("SuperBroker: There's no more children for this broker!");
										System.exit(1);
									}
									out.println(ports[i][childIndex]);
									childIndex++;
									break;
								}
								else{
									out.println("0");
								}
								
							}
						}
					}
					else if (inputLine.equals("I'm a Client. What is my port?")){
						//Check if the port number of Client beyond its range
						if (portForClients > 2018){
							System.err.println("SuperBroker: There's no more port availble for Client!");
							System.exit(1);
						}
						out.println(portForClients);
						currentPortForClient=portForClients;
						portForClients++;
					}
					else if (inputLine.equals("I'm a Client. What is my broker's port?")){
						//To find the corresponding entry in the structure table
						for (int i=0; i<15; i++){
							if (ports[i][0] == (currentPortForClient)){
								out.println(ports[i][1]);
								break;
							}
						}
					}
	            	inputLine = in.readLine();
	            }
	           
	            out.close();
	            in.close();
				clientSocket.close();
			}
		}
		
	}
		
}
