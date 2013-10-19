package buyer;

import javax.swing.JOptionPane;

public class BuyerClient {
    public static void main (String[] args) {
		// Ask user to enter host name and port number
		String hostName = JOptionPane.showInputDialog("Enter hostname: ");
		int portNumber = Integer.parseInt(JOptionPane.showInputDialog("Enter port number: "));
		
		///////////////////////////////////
		String[] options = {"Publish Bid", "Subscribe Interest", "Subscribe Interest Bid Update", "Subscribe Item Sold", "Set Automatic Mode"};
		 while (true) {
	     	
	     	// Ask user to select a function, function numbered starting from 0, -1 if the dialog is closed
	     	int function = JOptionPane.showOptionDialog(null, "Choose a buyer command to execute:", "Buyer", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
	     	
	     	
	     	// Quit program if the user closes the dialog box
	     	if (function == JOptionPane.CLOSED_OPTION) System.exit(0);
	     	
	     	switch(function) {
	     	case 0:
	     		//command: Publish Bid
	     		
	     		break;
	     	case 1:
	     		//command: Subscribe Interest
	     		
	     		break;
	     	case 2:
	     		//command: Subscribe Interest Bid Update
	     		
	     		break;
	     	case 3:
	     		//command: Subscribe Item Sold
	     		
	     		break;
	     	case 4:
	     		//command: Set Automatic Mode
	     	
	     		break;
	     	default: break;
	     	}
	     }
    }
}
