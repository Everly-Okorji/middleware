package broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BrokerSocket {

	ServerSocket serverSocket;
	Socket clientSocket;
	PrintWriter out;
	BufferedReader in;
	int port;
	
	BrokerSocket(int port) {
		
		this.port = port;
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println("Broker's server socket at port " + port + " encountered an I/O exception!");
			close();
		}
	}
	
	void accept() {
		// Create a client socket to accept connection to the server socket
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed for Broker's client socket at port" + port + "!");
            System.exit(1);
        }
	}

	void run() {
		
		// Instantiate output and input streams
        try {
			out = new PrintWriter(clientSocket.getOutputStream(), true);
	        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (IOException e) {
			System.err.println("I/O Exception at BrokerSocket's run method while instantiating PrintWriter and BufferedReader objects!");
			close();
		}
        
        // Wait for instructions and execute them
		String inputFromClient;
		try {
			while ((inputFromClient = in.readLine()) != null) {
				inputFromClient = in.readLine();
				BrokerServer.mHandler.addMessage(inputFromClient);
			}
		} catch (IOException e) {
			System.err.println("Input stream from client produced an I/O Exception!");
			close();
		}
		
		// Close streams
		try {
			in.close();
			out.close();
		} catch (IOException e) {
			System.err.println("Error while closing input stream in BrokerSocket class!");
			close();
		}
	}
	
	void close() {
		System.exit(1);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	void temp() {

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

		try {
			PrintWriter out1 = new PrintWriter(clientSocket1.getOutputStream(),
					true);
			BufferedReader in1 = new BufferedReader(new InputStreamReader(
					clientSocket1.getInputStream()));

			String inputLine1, outputLine1;
			Broker broker = new GeneralBroker();
			while (true) {
				inputLine1 = in1.readLine();

				if (inputLine1 != null) {

					String[] command = inputLine1.split("#");
					switch (command[0].charAt(0)) {
					case 'A':
						// Publish Available Item
						outputLine1 = ""
								+ broker.publishAvailableItem(
										Long.parseLong(command[2].substring(6)),
										Long.parseLong(command[3]), command[4],
										command[5],
										Float.parseFloat(command[6]));
						out1.println('A' + outputLine1);
						break;
					case 'B':
						// Publish Bid Update
						outputLine1 = ""
								+ broker.publishBidUpdate(
										Long.parseLong(command[2].substring(6)),
										Long.parseLong(command[3]));
						out1.println(outputLine1);
						break;
					case 'C':
						// Publish Finalize Sale
						outputLine1 = ""
								+ broker.publishFinalizeSale(
										Long.parseLong(command[2].substring(6)),
										Long.parseLong(command[3]),
										Float.parseFloat(command[4]),
										Long.parseLong(command[5]));
						out1.println(outputLine1);
						break;
					case 'D':
						// Subscribe Receive Bid
						outputLine1 = ""
								+ broker.subscribeReceiveBid(
										Long.parseLong(command[2].substring(6)),
										Long.parseLong(command[3]));
						out1.println(outputLine1);
						break;
					case 'E':
						// TODO
						// Look for seller with item and relay message
						// Return code for success or failure
						// Publish Bid
						// outputLine1 = "" +
						// broker.publishBid(Long.parseLong(command[2].substring(5)),
						// Long.parseLong(command[3]),Float.parseFloat(command[4]));
						// out1.println(outputLine1);

						break;
					case 'F':
						// Subscribe Interest
						outputLine1 = ""
								+ broker.subscribeInterest(
										Long.parseLong(command[2].substring(5)),
										command[3], command[4],
										Float.parseFloat(command[5]));
						out1.println(outputLine1);
						break;
					case 'G':
						// Subscribe Interest Bid Update
						outputLine1 = ""
								+ broker.subscribeInterestBidUpdate(
										Long.parseLong(command[2].substring(5)),
										Long.parseLong(command[3]));
						out1.println(outputLine1);
						break;
					case 'H':
						// Subscribe Item Sold
						outputLine1 = ""
								+ broker.subscribeItemSold(
										Long.parseLong(command[2].substring(5)),
										Long.parseLong(command[3]));
						out1.println(outputLine1);
						break;
					case 'Y':
						// Generate ID for new Seller/Buyer
						if ("Seller".equals(command[1])) {
							out1.println(broker.genSellerId());
						} else {
							out1.println(broker.genBuyerId());
						}
						break;
					case 'Z':
						// The client's session is out
						out1.close();
						in1.close();
						clientSocket1.close();
						serverSocket1.close();
						break;
					default:
						break;
					}

				}
			}

		} catch (IOException e) {
			System.err.println("Client Socket 1: Input/Output error.");
			System.exit(1);
		}
	}
}
