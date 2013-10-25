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
		if (contents.length < 3) {
			System.err.println("Message format is incorrect. Add '#' separators to message!");
			return;
		}
		if (contents[0].length() != 1) {
			System.err.println("First component of message must be a single character!");
		}

		long itemId, sellerId, buyerId, brokerId;
		int port;
		String name, attributes;
		float price, minimumBid, finalPrice;
		switch (contents[0].charAt(0)) {

		case 'A':	// A#Publish Available Item#Seller[Seller ID]#[Item ID]#[name]#[attributes]#[minimumBid]
			if (contents.length != 7) {
				System.err.println("'Publish Available Item' in Broker has too few or too many arguments!");
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
			
		case 'B':	// B#PublishBidUpdate#Seller[Seller ID]#Buyer[Buyer ID]#[Item ID]#[Price]
			if (contents.length != 6) {
				System.err.println("'Publish Bid Update' in Broker has too few or too many arguments!");
				return;
			}
			try {
				sellerId = Long.parseLong(contents[2].substring(6));
				buyerId = Long.parseLong(contents[3].substring(5));
				itemId = Long.parseLong(contents[4]);
				price = Float.parseFloat(contents[5]);
			} catch (NumberFormatException e) {
				System.err.println("One of the items in 'Publish Bid Update' for Broker is not a valid long number!");
				return;
			}
			BrokerServer.broker.publishBidUpdate(-1, sellerId, buyerId, itemId, price);
			break;
			
		case 'C':	// C#PublishFinalizeSale#Seller[Seller ID]#Buyer[Buyer ID]#[Item ID]#[Final Price]
			if (contents.length != 6) {
				System.err.println("'Publish Finalize Sale' in Broker has too few or too many arguments!");
				return;
			}
			try {
				sellerId = Long.parseLong(contents[2].substring(6));
				buyerId = Long.parseLong(contents[3].substring(5));
				itemId = Long.parseLong(contents[4]);
				finalPrice = Float.parseFloat(contents[5]);
			} catch (NumberFormatException e) {
				System.err.println("One of the items in 'Publish Finalize Sale' for Broker is not a valid long number!");
				return;
			}
			BrokerServer.broker.publishFinalizeSale(-1, sellerId, buyerId, itemId, finalPrice);
			break;
			
		case 'E':	// E#Publish Bid#Buyer[Buyer ID]#[Item ID]#[Price]
			if (contents.length != 5) {
				System.err.println("'Publish Bid' in Broker has too few or too many arguments!");
				return;
			}
			try {
				buyerId = Long.parseLong(contents[2].substring(5));
				itemId = Long.parseLong(contents[3]);
				price = Float.parseFloat(contents[4]);
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'PUblish Bid' in Broker is not a valid number!");
				return;
			}
			BrokerServer.broker.publishBid(buyerId, itemId, price);
			break;
			
		case 'F':	// F#Subscribe Interest#Buyer[Buyer ID]#[name]#[attributes]#[minimumBid]
			if (contents.length != 6) {
				System.err.println("'Subscribe Interest' in Broker has too few or too many arguments!");
				return;
			}
			try {
				buyerId = Long.parseLong(contents[2].substring(5));
				name = contents[3];
				attributes = contents[4];
				minimumBid = Float.parseFloat(contents[5]);
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'Subscribe Interest' in Broker is not a valid number!");
				return;
			}
			BrokerServer.broker.subscribeInterest(buyerId, name, attributes, minimumBid);
			break;
			
		case 'G':	// G#Subscribe Interest Bid Update#Buyer[Buyer ID]#[Item ID]
			if (contents.length != 4) {
				System.err.println("'Subscribe Interest Bid Update' in Broker has too few or too many arguments!");
				return;
			}
			try {
				buyerId = Long.parseLong(contents[2].substring(5));
				itemId = Long.parseLong(contents[3]);
			} catch (NumberFormatException e) {
				System.err.println("One of the numbers in 'Subscribe Interest Bid Update' in Broker is not a valid number!");
				return;
			}
			BrokerServer.broker.subscribeInterestBidUpdate(buyerId, itemId);
			break;
			
		case 'I':	// I#Broadcast Item#[Broker ID]#Seller[Seller ID]#[Item ID]#[name]#[attributes]#[minimumBid]
			if (contents.length != 8) {
				System.err.println("'Broadcast Item' in Broker has too few or too many arguments!");
				return;
			}
			try {
				brokerId = Long.parseLong(contents[2]);
				sellerId = Long.parseLong(contents[3].substring(6));
				itemId = Long.parseLong(contents[4]);
				name = contents[5];
				attributes = contents[6];
				minimumBid = Float.parseFloat(contents[7]);
			} catch (NumberFormatException e) {
				System.err.println("One of the items in 'Broadcast Item' for Broker is not a valid long number!");
				return;
			}
			BrokerServer.broker.broadcastItem(brokerId, sellerId, itemId, name, attributes, minimumBid);
			break;
			
		case 'J':	// J#PublishBidUpdate#[Broker ID]#Seller[Seller ID]#Buyer[Buyer ID]#[Item ID]#[Price]
			if (contents.length != 7) {
				System.err.println("The second 'Publish Bid Update' in Broker has too few or too many arguments!");
				return;
			}
			try {
				brokerId = Long.parseLong(contents[2]);
				sellerId = Long.parseLong(contents[3].substring(6));
				buyerId = Long.parseLong(contents[4].substring(5));
				itemId = Long.parseLong(contents[5]);
				price = Float.parseFloat(contents[6]);
			} catch (NumberFormatException e) {
				System.err.println("One of the items in the second 'Publish Bid Update' for Broker is not a valid long number!");
				return;
			}
			BrokerServer.broker.publishBidUpdate(brokerId, sellerId, buyerId, itemId, price);
			break;
			
		case 'K':	// K#Publish Finalize Sale#[Broker ID]#Seller[Seller ID]#Buyer[Buyer ID]#[Item ID]#[Final Price]
			if (contents.length != 7) {
				System.err.println("The second 'Publish Finalize Sale' in Broker has too few or too many arguments!");
				return;
			}
			try {
				brokerId = Long.parseLong(contents[2]);
				sellerId = Long.parseLong(contents[3].substring(6));
				buyerId = Long.parseLong(contents[4].substring(5));
				itemId = Long.parseLong(contents[5]);
				finalPrice = Float.parseFloat(contents[6]);
			} catch (NumberFormatException e) {
				System.err.println("One of the items in the second 'Publish Finalize Sale' for Broker is not a valid long number!");
				return;
			}
			BrokerServer.broker.publishFinalizeSale(brokerId, sellerId, buyerId, itemId, finalPrice);
			break;
		case 'Z':
			System.out.println("Port " + contents[2] + " closing...");
			break;
			
		default:
			System.err.println("Character code is invalid!");
			break;

		}
		

	}
}
