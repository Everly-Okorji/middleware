package broker;

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
			System.err.println("Message-handling interrupted while waiting for message!");
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

		long itemId, sellerId, buyerId;
		String name, attributes;
		float price, minimumBid, finalPrice;
		switch (contents[0].charAt(0)) {

		case 'A':	// A#Publish Available Item#Seller[Seller ID]#[Item ID]#[name]#[attributes]#[minimumBid]
			if (contents.length != 7) {
				System.err.println("'Publish Available Item' has too few or too many arguments!");
				return;
			}
			try {
				sellerId = Long.parseLong(contents[2].substring(6));
				itemId = Long.parseLong(contents[3]);
				name = contents[4];
				attributes = contents[5];
				minimumBid = Float.parseFloat(contents[6]);
			} catch (NumberFormatException e) {
				System.err.println("One of the items in 'Publish Available Item' for Broker is not a valid long number!");
				return;
			}
			BrokerServer.broker.publishAvailableItem(sellerId, itemId, name, attributes, minimumBid);
			break;
		case 'B':
			
			break;
			
		default:
			System.err.println("Character code is invalid!");
			break;

		}
		

	}
}
