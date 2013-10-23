package seller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class SellerClient {
	
	public static GeneralSeller seller;
	public static Socket sellerSocket;
	public static PrintWriter out;
	
	BufferedReader in;
	int userId;
	MessageHandler mHandler;
	Thread messageThread;
	
	public SellerClient() {
		
    	try {
    		sellerSocket = new Socket("localhost", 4444);
        	out = new PrintWriter(sellerSocket.getOutputStream(), true);
			in = new BufferedReader(
			    new InputStreamReader(sellerSocket.getInputStream()));
			
		} catch (IOException e) {
			System.out.println("Seller Socket or one of its components could not be created!");
			System.exit(1);
		}
    	
    	mHandler = new MessageHandler();
    	
	}

	public boolean getId() {
/*
		int result = JOptionPane.showConfirmDialog(null, "Are you a new user? ");

		switch (result) {
		case JOptionPane.CANCEL_OPTION:
			close(0);
			return false;
		case JOptionPane.YES_OPTION:
			String msg = "Y#Seller";
			out.println(msg);
			userId = Integer.parseInt(in.readLine());
			JOptionPane.showMessageDialog(null, "Your ID is: " + userId);
			return true;
		case JOptionPane.NO_OPTION:
			userId = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter your ID: "));
			return false;
		default:
			System.out.println("Could not determine code in Seller ID authentication!");
			close(1);
			return false;
		} */
		
		try {
			userId = Integer.parseInt(in.readLine());
		} catch (NumberFormatException e) {
			System.err.println("User ID is not a valid long number! System will shut down.");
			close(1);
			return false;
		} catch (IOException e) {
			System.err.println("I/O Exception while listening for a User Id");
			close(1);
			return false;
		}
		JOptionPane.showMessageDialog(null, "Your ID is: " + userId);
		return true;
		
	}
	
    public void run () {

    	// instantiate new seller
		seller = new GeneralSeller (userId, out);
		
		// Create new thread to handle messages
		messageThread = new Thread(new Runnable () {
			@Override
			public void run() {
				while (true) mHandler.handleNextMessage();
			}
		});
		messageThread.start();
		
		String fromUser;
		String[] options = { "Publish Available Item", "Finalize Sale"};
		
		while (true) {
			
			// Ask user to select a function, function numbered starting
			// from 0, -1 if the dialog is closed
			int function = JOptionPane
					.showOptionDialog(null,
							"Choose a seller command to execute:",
							"Seller", JOptionPane.DEFAULT_OPTION,
							JOptionPane.QUESTION_MESSAGE, null, options,
							options[0]);

			// Quit program if the user closes the dialog box
			if (function == JOptionPane.CLOSED_OPTION)
				close(0);

			long itemId, buyerId;
			String name, attributes;
			float minimumBid, finalPrice;

			switch (function) {
			case 0: // command: Publish Available Item

				// Fetch information from user
				name = JOptionPane.showInputDialog("Enter name of item: ");
				attributes = JOptionPane
						.showInputDialog("Enter a set of attributes, separated by commas: ");
				try {
					minimumBid = Float.parseFloat(JOptionPane
							.showInputDialog("Enter minimum bid: "));
				} catch (NumberFormatException e) {
					System.err
							.println("Minimum bid in 'Publish Available Item' must be a number!");
					break;
				}

				// Execute instruction in seller's class
				itemId = seller.publishAvailableItem(name, attributes, minimumBid);

				// Check if itemId is non-negative, and if not, return error
				// status
				if (itemId >= 0) {
					// Create message and pass to broker
					fromUser = "A#Publish Available Item#Seller" + userId
							+ "#" + itemId + "#" + name + "#" + attributes
							+ "#" + minimumBid;
					out.println(fromUser);
				}
				else{
					System.err.println("Item cannot be added to the list!");
				}
				break;

			case 1:
				// command: Finalize Sale
				try {
					itemId = new Long(
							JOptionPane.showInputDialog("Enter Item ID: "));
					finalPrice = new Float(
							JOptionPane
									.showInputDialog("Enter final price ($)"));
					buyerId = new Long(
							JOptionPane.showInputDialog("Enter Buyer ID: "));
				} catch (NumberFormatException e) {
					System.err
							.println("One of the inputs in 'Finalize Sale' is not a valid number!");
					break;
				}
				fromUser = "C#Publish Finalize Sale#Seller" + userId + "#"
						+ itemId + "#" + finalPrice + "#" + buyerId;
				out.println(fromUser);
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

