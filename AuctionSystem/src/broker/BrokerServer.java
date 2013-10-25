package broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class BrokerServer {

	final static int[] ALL_BROKER_PORTS = {2001, 2002, 2003, 2004, 2005, 2006};
	
	final int NUM_SOCKETS = 3;
	static GeneralBroker broker;
	int PORT_NUMBER;

	Socket socketToParent;
	static PrintWriter toParent;
	static BufferedReader fromParent;
	static int parentPort;
	static boolean hasParent;

	static MessageHandler mHandler;
	Thread mThread;
	
	static BrokerSocket[] brokerSockets;
	static int[] childPorts;
	ChildThread[] childThreads;

	// Parameters are hostname and port of SuperBroker
	public BrokerServer(String hostname, int port) {
		
		mHandler = new MessageHandler();
		
		// Handle messages
		mThread = new Thread (new Runnable() {
			@Override
			public void run() {
				while (true) mHandler.handleNextMessage();
			}
		});
		
		Socket connectToSB;
		PrintWriter out;
		BufferedReader in;
		try {
			// Connect to super broker
			connectToSB = new Socket(hostname, port);
			out = new PrintWriter(connectToSB.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(
					connectToSB.getInputStream()));

			// Get parent port
			out.println("I'm a Broker. What is my parent's port?");
			String parent = in.readLine();
			if ("0".equals(parent)) {
				parentPort = -1;
				hasParent = false;
			} else {
				parentPort = Integer.parseInt(parent);
				hasParent = true;
			}

			// Get children ports
			for (int i = 0; i < NUM_SOCKETS; i++) {
				out.println("I'm a Broker. What is my child's port?");
				String child = in.readLine();
				if ("0".equals(child)) {
					childPorts[i] = -1;
				} else {
					childPorts[i] = Integer.parseInt(child);
				}
			}
			
			out.println("I'm done.");
		} catch (IOException e1) {
			close();
		}

		// Instantiate classes for server sockets and accept() on three separate
		// threads
		brokerSockets = new BrokerSocket[NUM_SOCKETS];
		for (int i = 0; i < NUM_SOCKETS; i++) {
			if (childPorts[i] > 0) {
				brokerSockets[i] = new BrokerSocket(childPorts[i]);
				childThreads[i] = new ChildThread(brokerSockets[i], i);
				childThreads[i].start();
			}
		}

		if (hasParent) {
			// Initiate connection with parent
			try {
				socketToParent = new Socket(hostname, parentPort);
				toParent = new PrintWriter(socketToParent.getOutputStream(),
						true);
				fromParent = new BufferedReader(new InputStreamReader(
						socketToParent.getInputStream()));
			} catch (UnknownHostException e) {
				System.err
						.println("Unknown host at BrokerServer while connecting to parent port!");
				close();

			} catch (IOException e) {
				System.err
						.println("I/O Exception while instantiating BrokerServer's socket!");
				close();
			}
		}

	}

	void run() {
		
		// Handle messages
		mThread.start();
		
		// Fetch messages from parent
		String inputFromParent;
		if (hasParent) {
			try {
				while ((inputFromParent = fromParent.readLine()) != null) {
					mHandler.addMessage(inputFromParent + "#" + parentPort);
				}
			} catch (IOException e1) {
				System.err.println("I/O Exception found while fetching messages from broker's parent");
			}

			try {
				socketToParent.close();
				toParent.close();
				fromParent.close();
			} catch (IOException e) {
				System.err
						.println("Error closing I/O handles in BrokerServer!");
				close();
			}
		}
	}

	void close() {

    	mThread.stop();
    	
    	// Close IO handles
    	try {
    		if (fromParent != null) fromParent.close();
			if (toParent != null) toParent.close();
		} catch (IOException e) {
			System.exit(1);
		}

		System.exit(1);
	}

}
