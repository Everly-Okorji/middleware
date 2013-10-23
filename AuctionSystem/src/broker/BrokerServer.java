package broker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class BrokerServer {

	final int NUM_SOCKETS = 3;

	Socket socketToParent;
	PrintWriter toParent;
	BufferedReader fromParent;
	int parentPort;
	boolean hasParent;

	BrokerSocket[] brokerSockets;
	int[] childPorts;
	ChildThread[] childThreads;

	// Parameters are hostname and port of SuperBroker
	BrokerServer(String hostname, int port) {

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
			out.println("What is my parent's port?");
			String parent = in.readLine();
			if ("null".equals(parent)) {
				parentPort = -1;
				hasParent = false;
			} else {
				parentPort = Integer.parseInt(parent);
				hasParent = true;
			}

			// Get children ports
			for (int i = 0; i < NUM_SOCKETS; i++) {
				out.println("Child Port " + i);
				String child = in.readLine();
				if ("null".equals(child)) {
					childPorts[i] = -1;
				} else {
					childPorts[i] = Integer.parseInt(child);
				}
			}
		} catch (IOException e1) {
			close();
		}

		// Instantiate classes for server sockets and accept() on three separate
		// threads
		brokerSockets = new BrokerSocket[NUM_SOCKETS];
		for (int i = 0; i < NUM_SOCKETS; i++) {
			if (childPorts[i] > 0)
				brokerSockets[i] = new BrokerSocket(childPorts[i]);
			childThreads[i] = new ChildThread(brokerSockets[i], i);
			childThreads[i].start();
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

	}

	void close() {
		System.exit(1); // TODO
	}

}
