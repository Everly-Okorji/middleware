package seller;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageHandler {

	BlockingQueue<String> messages;

	MessageHandler() {
		messages = new LinkedBlockingQueue<String>();
	}

	void addMessage(String message) {
		messages.add(message);
	}

	void handleNextMessage() {

		String message = null;
		try {
			message = messages.take();
		} catch (InterruptedException e) {
			System.err.println("Message-handling thread interrupted while waiting for message!");
		}

		if (message == null)
			return;

		String[] contents = message.split("#");
		if (contents.length < 2) {
			System.err.println("Message format is incorrect. Add '#' separators to message!");
			return;
		}
		if (contents[0].length() != 1) {
			System.err.println("First component of message must be a single character!");
		}

		long itemId, buyerId;
		float price;
		switch (contents[0].charAt(0)) {

		case 'A':			// A#Publish Available Item#[Item ID]
			if (contents.length != 3) {
				System.err.println("'Publish Available Item' has too few or too many arguments!");
				return;
			}
			try {
				itemId = Long.parseLong(contents[2]);
			} catch (NumberFormatException e) {
				System.err.println("Item ID in 'Publish Available Item' is not a valid long number!");
				return;
			}
			SellerClient.seller.confirmAddItem(itemId);
			break;
			
		case 'B':			// B#Publish Bid Update#[Buyer ID]#[Item ID]#[Price]
			if (contents.length != 5) {
				System.err.println("'Publish Bid Update' has too few or too many arguments!");
				return;
			}
			try {
				buyerId = Long.parseLong(contents[2]);
				itemId = Long.parseLong(contents[3]);
				price = Float.parseFloat(contents[4]);
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'Publish Bid Update' is not a valid long number!");
				return;
			}
			SellerClient.seller.confirmBidUpdate(buyerId, itemId, price);
			break;
			
		case 'C':			// C#Publish Finalize Sale#[Buyer ID]#[Item ID]#[Final Price]
			if (contents.length != 5) {
				System.err.println("'Publish Finalize Sale' has too few or too many arguments!");
				return;
			}
			try {
				buyerId = Long.parseLong(contents[2]);
				itemId = Long.parseLong(contents[3]);
				price = Float.parseFloat(contents[4]);
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'Publish Finalize Sale' is not a valid number!");
				return;
			}
			SellerClient.seller.finalizeSale(buyerId, itemId, price);
			break;
			
		case 'D':			// D#Subscribe Receive Bid#[Buyer ID]#[Item ID]#[Price]
			if (contents.length != 5) {
				System.err.println("'Subscribe Receive Bid' has too few or too many arguments!");
				return;
			}
			try {
				buyerId = Long.parseLong(contents[2]);
				itemId = Long.parseLong(contents[3]);
				price = Float.parseFloat(contents[4]);
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'Subscribe Receive Bid' is not a valid number!");
				return;
			}
			SellerClient.seller.bid(buyerId, itemId, price);
			break;
			
		default:
			System.err.println("Character code is invalid!");
			break;

		}

	}
}
