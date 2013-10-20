package broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BrokerServer {
	public static void main (String[] args) {
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
				ServerSocket serverSocket1 = null;
				try {
		            serverSocket1 = new ServerSocket(4444);
		        } catch (IOException e) {
		            System.err.println("Socket 1: Could not listen on port: 4444.");
		            System.exit(1);
		        }
				
			   Socket clientSocket1 = null;
		        try {
		            clientSocket1 = serverSocket1.accept();
		        } catch (IOException e) {
		            System.err.println("Client Socket 1: Accept failed.");
		            System.exit(1);
		        }
				
		        try{
		        	PrintWriter out1 = new PrintWriter(clientSocket1.getOutputStream(), true);
			        BufferedReader in1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
			        
			        String inputLine, outputLine;
			        Broker broker = new GeneralBroker();
			        
			        while(true) {
			        	inputLine = in1.readLine();
			        	
			        	if (inputLine!=null){       	
			        		
			        		String[] command = inputLine.split("#");
			        		switch (command[0].charAt(0)){
			        		case 'A':
			        			
			        			outputLine = "" + broker.publishAvailableItem(Long.parseLong(command[2]), Long.parseLong(command[3]), command[4], command[5],Float.parseFloat(command[6]));
			        			out1.println(outputLine);
			        			
			        			break;
			        			
			        		case 'Y':
			        			//Generate ID for new Seller/Buyer
			        			if ("Seller".equals(command[1])){
			        				out1.println(broker.genSellerId());
			        			}
			        			else{
			        				out1.println(broker.genBuyerId());
			        			}
			        			break;
			        		case 'Z':
			        			//The client's session is out
			        			out1.close();
			    		        in1.close();
			    		        clientSocket1.close();
			    		        serverSocket1.close();
			        			break;
			        		default: break;
			        		}
			        		
			        		
			        	}
			        }
			        
		        } catch (IOException e){
		        	System.err.println("Client Socket 1: Input/Output error.");
		            System.exit(1);
		        }	   
		        
			}
			
		}).start();
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				
			}
			
		}).start();      


        
	}
}
