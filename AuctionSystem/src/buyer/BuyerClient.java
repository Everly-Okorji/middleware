package buyer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class BuyerClient {
	
	public static GeneralBuyer buyer;
	public static Socket buyerSocket;
	public static PrintWriter out;
	BufferedReader in;
	
	int userId;
	int parentPort;
	
	MessageHandler mHandler;
	Thread messageThread;
	
	public BuyerClient() {
	
    	try {
    		buyerSocket = new Socket("localhost", 2000);
        	out = new PrintWriter(buyerSocket.getOutputStream(), true);
			in = new BufferedReader(
			    new InputStreamReader(buyerSocket.getInputStream()));
			getId();
			out.println("I'm a Client. What is my broker's port?");
			parentPort = Integer.parseInt(in.readLine());
			buyerSocket = new Socket("localhost", parentPort);
        	out = new PrintWriter(buyerSocket.getOutputStream(), true);
			in = new BufferedReader(
			    new InputStreamReader(buyerSocket.getInputStream()));
		} catch (IOException e) {
			System.out.println("Buyer Socket or one of its components could not be created!");
			System.exit(1);
		} catch (NumberFormatException e) {
			System.err.println("Parent Port is not a valid int number! System will shut down.");
			out.println("I'm done.");
			close(1);
		}
    	
    	mHandler = new MessageHandler();
    	
	}

	public boolean getId() {

		try {
			out.println("I'm a Client. What is my port?");
			userId = Integer.parseInt(in.readLine());
		} catch (NumberFormatException e) {
			System.err.println("User ID is not a valid long number! System will shut down.");
			out.println("I'm done.");
			close(1);
			return false;
		} catch (IOException e) {
			System.err.println("I/O Exception while listening for a User Id");
			out.println("I'm done.");
			close(1);
			return false;
		}
		JOptionPane.showMessageDialog(null, "Your ID is: " + userId);
		return true;
		
	}
	
    public void run () {

    	// instantiate new seller
		buyer = new GeneralBuyer (userId, out);
		
		// Create new thread to handle messages
		messageThread = new Thread(new Runnable () {
			@Override
			public void run() {
				while (true) mHandler.handleNextMessage();
			}
		});
		messageThread.start();
		
		String fromUser;
		String[] options = { "Subscribe Interest", "Bid", "Subscribe Interest Bid Update", "Set Mode"};
		
		while (true) {
			
			// Ask user to select a function, function numbered starting
			// from 0, -1 if the dialog is closed
			int function = JOptionPane
					.showOptionDialog(null,
							"Choose a buyer command to execute:",
							"Buyer", JOptionPane.DEFAULT_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);

			// Quit program if the user closes the dialog box
			if (function == JOptionPane.CLOSED_OPTION)
				close(0);

			long itemId;
			String name, attributes;
			float minimumBid, price, maximumBid, increment;

			switch (function) {
			case 0: // command: Subscribe Interest

				// Fetch information from user
				name = JOptionPane.showInputDialog("Enter name of item (or leave blank): ");
				attributes = JOptionPane.showInputDialog("Enter a set of attributes, separated by commas (or leave blank): ");
				try {
					String minimumBidString = JOptionPane.showInputDialog("Enter minimum bid (or leave blank): ");
					if (minimumBidString.isEmpty()) minimumBid = 0.0f;
					else minimumBid = Float.parseFloat(minimumBidString);
				} catch (NumberFormatException e) {
					System.err
							.println("Minimum bid in 'Subscribe Interest' must be a valid number!");
					break;
				}
				fromUser = "F#Subscribe Interest#Buyer" + userId + "#" + name + "#" + attributes + "#" + minimumBid;
				out.println(fromUser);
				break;

			case 1:	// command: Publish Bid
				try {
					itemId = new Long(
							JOptionPane.showInputDialog("Enter Item ID for an item to place bid on: "));
					price = new Float(
							JOptionPane.showInputDialog("Enter price for the bid: "));
				} catch (NumberFormatException e) {
					System.err
							.println("One of the inputs in 'Publish Bid' (in Buyer) is not a valid number!");
					break;
				}
				BuyerClient.buyer.bid(itemId, price);
				break;
			case 2:
				// command: Subscribe Interest Bid Update
				try {
					itemId = new Long(
							JOptionPane.showInputDialog("Enter Item ID for an item to subscribe to bid updates: "));
				} catch (NumberFormatException e) {
					System.err
							.println("One of the inputs in 'Subscribe Interest Bid Update' (in Buyer) is not a valid number!");
					break;
				}
				fromUser = "G#Subscribe Interest Bid Update#Buyer" + userId + "#" + itemId;
				out.println(fromUser);
				break;
				
			case 3:
				String[] modes = {"User-Driven Mode", "Automatic Mode"};
				int mode = JOptionPane.showOptionDialog(null, "Select user mode: ", "Select Mode", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);
				
				if (mode == 0) {
					buyer.setUserDrivenMode();
				} else if (mode == 1) {
					try {
						maximumBid = new Float(JOptionPane.showInputDialog("Enter maximum bid: "));
						increment = new Float(JOptionPane.showInputDialog("Enter increment (i.e. greater than 0): "));
					} catch (NumberFormatException e) {
						System.err
								.println("One of the inputs in 'Automatic Mode' (in Buyer) is not a valid number!");
						break;
					}
					buyer.setAutomaticMode(maximumBid, increment);
				} else {
					System.err.println("User closed the dialog box. System will close now.");
					close(0);
				}
				break;

			default:
				System.err.println("Invalid command!");
				break;
			}
			
			// Get all pending messages from broker
	    	try {
				while (in.ready()) {
					String message = in.readLine();
					mHandler.addMessage(message);
				}
			} catch (IOException e) {
				System.err.println("Buffered Reader error! System will close now.");
				close(1);
			}
	    	
		}
    }
   
    public void close(int code) {
    	
    	// Tell server to close connection
    	String fromUser = "Z#Quit";
    	out.println(fromUser);
    	
    	messageThread.stop();
    	
    	// Close IO handles
    	try {
    		if (out != null) out.close();
			if (in != null) in.close();
		} catch (IOException e) {
			System.exit(1);
		}

    	// Exit
    	System.exit(code);
    }

}

