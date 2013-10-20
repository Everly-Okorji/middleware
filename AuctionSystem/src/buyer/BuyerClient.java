package buyer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import buyer.Item;

public class BuyerClient {
	public static void main(String[] args) {
    	
    	// Keeps track of the items listed by the seller
    	List<Item> items = new ArrayList<Item>();
    	long lastItemId = 0;
    	
		int userId = -1;

		String hostName = "localhost";
		int portNumber = 4445;

		// Create connection to socket and fetch input and output streams
		try (Socket buyerSocket = new Socket(hostName, portNumber);
				PrintWriter out = new PrintWriter(
						buyerSocket.getOutputStream(), true);
				BufferedReader in = new BufferedReader(new InputStreamReader(
						buyerSocket.getInputStream()));) {

			int result = JOptionPane.showConfirmDialog(null,
					"Are you a new user? ");

			if (result == JOptionPane.CANCEL_OPTION) {
				close(out, in, buyerSocket, 0);
			} else if (result == JOptionPane.YES_OPTION) {
				String msg = "Y#Buyer";
				out.println(msg);
				userId = Integer.parseInt(in.readLine());
				JOptionPane.showMessageDialog(null, "Your ID is: " + userId);

			} else if (result == JOptionPane.NO_OPTION) {
				userId = Integer.parseInt(JOptionPane.showInputDialog(null,
						"Enter your ID: "));
			} else {
				System.out
						.println("Could not determine code in Buyer ID authentication!");
				close(out, in, buyerSocket, 1);
			}

			String fromUser;

			String[] options = { "Publish Bid", "Subscribe Interest",
					"Subscribe Interest Bid Update", "Subscribe Item Sold",
					"Set Automatic Mode" };
			while (true) {

				// Ask user to select a function, function numbered starting
				// from 0, -1 if the dialog is closed
				int function = JOptionPane
						.showOptionDialog(null,
								"Choose a buyer command to execute:", "Buyer",
								JOptionPane.DEFAULT_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);

				// Quit program if the user closes the dialog box
				if (function == JOptionPane.CLOSED_OPTION)
					System.exit(0);

				long itemId = -1;
				String name;
				String attributes;
				float minimumBid;
				double price;
				int status = -1;

				switch (function) {
				case 0:
					// command: Publish Bid
					itemId = new Long(JOptionPane.showInputDialog("Enter Item ID:"));
					price = new Double (JOptionPane.showInputDialog("Enter price: $"));
					fromUser = "E#Publish Bid#Buyer" + userId + "#" + itemId + "#" + price;
					status = send(fromUser, out, in);
					break;
					
				case 1:
					// command: Subscribe Interest
					itemId = lastItemId++;
					name = JOptionPane.showInputDialog("Enter name of item: ");
					attributes = JOptionPane
							.showInputDialog("Enter a set of attributes, separated by commas: ");
					minimumBid = Float.parseFloat(JOptionPane
							.showInputDialog("Enter minimum bid: "));

					Set<String> attr = new HashSet<String>(
							Arrays.asList(attributes.split(",")));

					// Store data in list
					items.add(new Item(itemId, name, attr, minimumBid));
					
					fromUser = "F#Subscribe Interest#Buyer" + userId + "#" + name + "#" + attributes + "#" + minimumBid;
					status = send(fromUser, out, in);
					break;
					
				case 2:
					// command: Subscribe Interest Bid Update
					itemId = new Long(JOptionPane.showInputDialog("Enter Item ID:"));
					fromUser = "G#Subscribe Interest Bid Update#Buyer" + userId + "#" + itemId;
					status = send(fromUser, out, in);
					break;
				case 3:
					// command: Subscribe Item Sold
					itemId = new Long(JOptionPane.showInputDialog("Enter Item ID:"));
					fromUser = "H#Subscribe Item Sold#Buyer" + userId + "#" + itemId;
					status = send(fromUser, out, in);
					break;
				case 4:
					// command: Set Automatic Mode
					// TODO
					break;
				default:
					break;
				}

				if (status == 0) {
					JOptionPane.showMessageDialog(null,
							"Execution Complete! Item ID is " + itemId);
				} else {
					if (status == 1) {
						JOptionPane
								.showMessageDialog(null,
										"Server unresponsive! System will shut down now.");
						close(out, in, buyerSocket, 1);
					} else
						JOptionPane.showMessageDialog(null,
								"Server returned unexpected message!");
				}
				
				status = -1;
			}

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to "
					+ hostName);
			System.exit(1);
		}
	}

	private static int send(String fromUser, PrintWriter out, BufferedReader in)
			throws IOException {

		out.println(fromUser);
		String fromServer = in.readLine();
		if ("0".equals(fromServer)) {
			return 0;
		} else {
			if (fromServer == null) {
				return 1;
			}
			return 2;
		}
	}

	public static void close(PrintWriter out, BufferedReader in,
			Socket buyerSocket, int code) {

		// Tell server to close connection
		String fromUser = "Z#Quit";
		out.println(fromUser);

		// Close IO handles
		try {
			if (out != null)
				out.close();
			if (in != null)
				in.close();
		} catch (IOException e) {
			System.exit(1);
		}

		// Exit
		System.exit(code);
	}
}
