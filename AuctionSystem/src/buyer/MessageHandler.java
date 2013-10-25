package buyer;

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
			System.err.println("Message-handling thread interrupted while waiting for message in Buyer!");
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
		String name, attributes;
		switch (contents[0].charAt(0)) {

		case 'H':			// H#Subscribe Item Sold#[Buyer ID]#[Item ID]#[Final Price]
			if (contents.length != 5) {
				System.err.println("'Subscribe Item Sold' in Buyer has too few or too many arguments!");
				return;
			}
			try {
				buyerId = Long.parseLong(contents[2]);
				itemId = Long.parseLong(contents[3]);
				price = Float.parseFloat(contents[4]);
				
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'Subscribe Item Sold' in Buyer is not a valid long number!");
				return;
			}
			BuyerClient.buyer.processItemSold(buyerId, itemId, price);
			break;
			
		case 'G':			// G#Subscribe Interest Bid Update#[Buyer ID]#[Item ID]#[Price]
			if (contents.length != 5) {
				System.err.println("'Subscribe Interest Bid Update' in Buyer has too few or too many arguments!");
				return;
			}
			try {
				buyerId = Long.parseLong(contents[2]);
				itemId = Long.parseLong(contents[3]);
				price = Float.parseFloat(contents[4]);
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'Subscribe Interest Bid Update' in Buyer is not a valid long number!");
				return;
			}
			BuyerClient.buyer.processBidUpdate(buyerId, itemId, price);
			break;
			
		case 'F':			// F#Subscribe Interest#[Item ID]#[name]#[attributes]#[Current Bid]	--> confirmation mesage
			if (contents.length != 6) {
				System.err.println("'Subscribe Interest' in Buyer has too few or too many arguments!");
				return;
			}
			try {
				itemId = Long.parseLong(contents[2]);
				name = contents[3];
				attributes = contents[4];
				price = Float.parseFloat(contents[5]);
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'Subscribe Interest' in Buyer is not a valid number!");
				return;
			}
			BuyerClient.buyer.processInterest(itemId, name, attributes, price);
			break;
			
		case 'E':			// E#Publish Bid#[Buyer ID]#[Item ID]#[Price] --> confirmation message
			if (contents.length != 5) {
				System.err.println("'Publish Bid' in Buyer has too few or too many arguments!");
				return;
			}
			try {
				buyerId = Long.parseLong(contents[2]);
				itemId = Long.parseLong(contents[3]);
				price = Float.parseFloat(contents[4]);
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'Publish Bid' in Buyer is not a valid number!");
				return;
			}
			BuyerClient.buyer.confirmBid(buyerId, itemId, price);
			break;
			
		default:
			System.err.println("Character code is invalid in Buyer!");
			break;

		}

	}
}
