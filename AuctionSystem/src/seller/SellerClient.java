package seller;

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

public class SellerClient {
	
    public static void main (String[] args) {

    	// Keeps track of the items listed by the seller
    	List<Item> items = new ArrayList<Item>();
    	long lastItemId = 0;
    	
    	// Ask user to enter host name and port number
    	String hostName = "localhost";
    	int portNumber = 4444;

    	// Create connection to socket and fetch input and output streams
        try (
            Socket sellerSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(sellerSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                new InputStreamReader(sellerSocket.getInputStream()));
        ) {
           
            String fromServer;
            String fromUser;
            
        	String[] options = {"Publish Available Item", "Publish Bid Update", "Publish Finalize Sale", "Subscribe Receive Bid"};
            while (true) {
            	
            	// Ask user to select a function, function numbered starting from 0, -1 if the dialog is closed
            	int function = JOptionPane.showOptionDialog(null, "Choose a seller command to execute:", "Seller", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            	
            	
            	// Quit program if the user closes the dialog box
            	if (function == JOptionPane.CLOSED_OPTION) System.exit(0);
            	
            	switch(function) {
            	case 0:
            		//command: Publish Available Item
            		
            		// Fetch data from user
            		Long itemId = lastItemId++;
            		String name = JOptionPane.showInputDialog("Enter name of item: ");
            		String attributes = JOptionPane.showInputDialog("Enter a set of attributes, separated by commas: ");
            		float minimumBid = Float.parseFloat(JOptionPane.showInputDialog("Enter minimum bid: "));
            		
            		Set<String> attr = new HashSet<String>(Arrays.asList(attributes.split(",")));
            		
            		// Store data in list
            		items.add(new Item(itemId, name, attr, minimumBid));
            		
            		fromUser = "A|Publish Available Item|" + itemId + "|" + name + "|" + attributes + "|" + minimumBid;
            		out.println(fromUser);
            		
            		fromServer = in.readLine();
            		if ("Done".equals(fromServer)) {
            			JOptionPane.showMessageDialog(null, "Item published! Item ID is " + itemId);
            		} else {
            			if (fromServer == null) {
            				JOptionPane.showMessageDialog(null, "Server unresponsive! System will shut down now.");
            				System.exit(1);
            			}
            			JOptionPane.showMessageDialog(null, fromServer);
            		}
            		
            		break;
            	case 1:
            		//command: Publish Bid Update
            		
            		break;
            	case 2:
            		//command: Publish Finalize Sale
            		
            		break;
            	case 3:
            		//command: Subscribe Receive Bid
            		
            		break;
            	default: break;
            	}
            }
           
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                hostName);
            System.exit(1);
        }
    }
   
}

