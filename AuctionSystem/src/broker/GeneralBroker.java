package broker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GeneralBroker {

	int portNo;
	boolean hasParent;
	int parentPort;
	List<Item> localItems; // Local list of items
	List<Item> importedItems;	// Items that come from a child broker
	Map<Long, Long> itemIdToSender;  // Keeps track of sender of new items
	Map<Long, Integer> itemIdToBroker;	// Keep track of broker in charge of item

	GeneralBroker(int port, boolean hasParent, int parentPort) {
		portNo = port;
		this.hasParent = hasParent;
		this.parentPort = parentPort;
		importedItems = new ArrayList<Item>();
		localItems = new ArrayList<Item>();
		itemIdToSender = new HashMap<Long, Long>();
		itemIdToBroker = new HashMap<Long, Integer>();
		
	}

	public long getId() {
		return portNo;
	}

	// From Seller to Broker
	public void publishAvailableItem(long sellerId, long itemId, String name,
			String attributes, float minimumBid) {

		// Add item to list
		Set<String> attr = new HashSet<String>(Arrays.asList(attributes.split(",")));
		Item item = new Item(sellerId, itemId, name, attr, minimumBid);
		localItems.add(item);
		
		itemIdToSender.put(itemId, Long.parseLong("" + portNo));
		
		// Send item up to parent broker if parent exists
		if (hasParent) {
			// I#Broadcast Item#[Broker ID]#[Seller ID]#[Item ID]#[name]#[attributes]#[minimumBid]
			String itemMessage = "I#Broadcast Item#" + portNo + "#" + sellerId + "#" + itemId + "#" + name + "#" + attributes + "#" + minimumBid;
			sendMsg(itemMessage, parentPort);
		}
		
		// Send confirmation message to the appropriate client (i.e. seller)
		String confirmationMsg = "A#Publish Available Item#" + itemId;
		sendMsg(confirmationMsg, Integer.parseInt("" + sellerId));

	}
	
	// From Broker to Parent Broker. Broker ID is the id of the broker who sent this item to me
	public void broadcastItem(long brokerId, long sellerId, long itemId, String name,
			String attributes, float minimumBid) {
		
		// Add item to list of items from children brokers
		Set<String> attr = new HashSet<String>(Arrays.asList(attributes.split(",")));
		Item item = new Item(sellerId, itemId, name, attr, minimumBid);
		importedItems.add(item);
		
		itemIdToSender.put(itemId, brokerId);
		
		// I#Broadcast Item#[Broker ID]#Seller[Seller ID]#[Item ID]#[name]#[attributes]#[minimumBid]
		String itemMessage = "I#Broadcast Item#" + portNo + "#Seller" + sellerId + "#" + itemId + "#" + name + "#" + attributes + "#" + minimumBid;
		
		// Send item up to parent broker if parent exists
		if (hasParent) {
			sendMsg(itemMessage, parentPort);
		}
		
		// Send item to any other child brokers
		for (int i = 0; i < BrokerServer.childPorts.length; i++) {
			if (BrokerServer.childPorts[i] != -1) {
				// If the port is a broker port and is not the port you received the message from, send message
				if (Arrays.asList(BrokerServer.ALL_BROKER_PORTS).contains(BrokerServer.childPorts[i]) && (!Long.valueOf(BrokerServer.childPorts[i]).equals(Long.valueOf(brokerId)))) {
					sendMsg(itemMessage, BrokerServer.childPorts[i]);
				}
			}
		}
		
	}

	// From Buyer to Broker
	public void publishBid(long buyerId, long itemId, float price) {

		// itemIdToPort.put(itemId, port);

		String bidMsg = "E#Publish Bid#Buyer" + buyerId + "#" + itemId + "#" + price;

		long sender = itemIdToSender.get(itemId);

		// Check if I have the item in my local list (i.e. I am the 'sender')
		if (Long.valueOf(sender).equals(Long.valueOf(portNo))) {

			long sellerId = -1;

			// Find item
			for (Item item_temp : localItems) {
				if (Long.valueOf(item_temp.itemId).equals(Long.valueOf(itemId))) {
					// Get seller id
					sellerId = item_temp.sellerId;
				}
			}

			if (Long.valueOf(sellerId).equals(Long.valueOf(-1))) {
				System.err
						.println("System Error. Seller ID was not found in publishBid method in GeneralBroker class!");
			}

			// Find seller
			for (int i = 0; i < BrokerServer.childPorts.length; i++) {
				if (Long.valueOf(BrokerServer.childPorts[i]).equals(
						Long.valueOf(sellerId))) {
					BrokerServer.brokerSockets[i].out.println(bidMsg);
				}
			}
		} else { // I don't have the item in my local list
			if (hasParent
					&& (Long.valueOf(parentPort).equals(Long.valueOf(sender)))) {
				BrokerServer.toParent.println(bidMsg);
			} else {
				// One of my other child brokers must be the sender
				for (int i = 0; i < BrokerServer.childPorts.length; i++) {
					if (Long.valueOf(BrokerServer.childPorts[i]).equals(
							Long.valueOf(sender))) {
						BrokerServer.brokerSockets[i].out.println(bidMsg);
						break;
					}
				}
			}
		}

	}

	
	public void publishBidUpdate(long brokerId, long sellerId, long buyerId, long itemId, float price) {
		
		// E#Publish Bid#[Buyer ID]#[Item ID]#[Price]
		String bidUpdateMsgToBuyer = "D#Subscribe Receive Bid#" + buyerId + "#" + itemId + "#" + price;
		
		// Send bid confirmation to buyer if buyer is one of the broker's clients
		for (int i = 0; i < BrokerServer.childPorts.length; i++) {
			if (Long.valueOf(BrokerServer.childPorts[i]).equals(Long.valueOf(buyerId))) {
				BrokerServer.brokerSockets[i].out.println(bidUpdateMsgToBuyer);
				break;
			}
		}
		
		boolean bidUpdated = false;
		Item currentItem = null;
		
		// Update bid on broker
		for (Item item: localItems) {
			if (Long.valueOf(item.itemId).equals(Long.valueOf(itemId))) {
				currentItem = item;
				item.addToSaleSubscription(buyerId);
				if (!item.makeBid(buyerId, price)) {
					System.err.println("Error: Bid Update registered a bid lower than the current price (see GeneralBroker)!");
				}
				bidUpdated = true;
				break;
			}
		}
		
		if (!bidUpdated) {
			for (Item item: importedItems) {
				if (Long.valueOf(item.itemId).equals(Long.valueOf(itemId))) {
					currentItem = item;
					if (!item.makeBid(buyerId, price)) {
						System.err.println("Error: Bid Update (looping through imported items) registered a bid lower than the current price (see GeneralBroker)!");
					}
					bidUpdated = true;
					break;
				}
			}
		}
		
		// Pass on message to other brokers:
		// J#PublishBidUpdate#[Broker ID]#Seller[Seller ID]#Buyer[Buyer ID]#[Item ID]#[Price]
		String bidUpdateMsgToBroker = "J#Publish Bid Update#" + brokerId + "#Seller" + sellerId + "#Buyer" + buyerId + "#" + itemId + "#" + price;
		
		// Send message up to parent broker if parent exists
		if (hasParent) {
			sendMsg(bidUpdateMsgToBroker, parentPort);
		}
		
		// Send item to any other child brokers
		for (int i = 0; i < BrokerServer.childPorts.length; i++) {
			if (BrokerServer.childPorts[i] != -1) {
				// If the port is a broker port and is not the port you received the message from, send message
				if (Arrays.asList(BrokerServer.ALL_BROKER_PORTS).contains(BrokerServer.childPorts[i]) && (!Long.valueOf(BrokerServer.childPorts[i]).equals(Long.valueOf(brokerId)))) {
					sendMsg(bidUpdateMsgToBroker, BrokerServer.childPorts[i]);
				}
			}
		}
		
		// Send out bid update message to all buyers subscribed to the item
		String bidUpdateToBuyers = "G#Subscribe Interest Bid Update#" + buyerId + "#" + itemId + "#" + price;
		
		// Loop through children
		for (int i = 0; i < BrokerServer.childPorts.length; i++) {
			if (BrokerServer.childPorts[i] != -1) {
				// For each child, loop through subscribers to bid update
				Iterator<Long> it = currentItem.getSubscribersToBidUpdate().iterator();
				while (it.hasNext()) {
					// If a child is a subscriber, send bid update
					if (Long.valueOf(BrokerServer.childPorts[i]).equals(Long.valueOf(it.next()))) {
						sendMsg(bidUpdateToBuyers, BrokerServer.childPorts[i]);
					}
				}
				
			}
		}
		
		
	}

	public void publishFinalizeSale(long brokerId, long sellerId,  long buyerId, long itemId,
			float finalPrice) {
		
		boolean saleFinalized = false;
		Item currentItem = null;
		
		// Update bid on broker
		for (Item item: localItems) {
			if (Long.valueOf(item.itemId).equals(Long.valueOf(itemId))) {
				currentItem = item;
				item.finalizeSale();
				saleFinalized = true;
				break;
			}
		}
		
		if (!saleFinalized) {
			for (Item item: importedItems) {
				if (Long.valueOf(item.itemId).equals(Long.valueOf(itemId))) {
					currentItem = item;
					item.finalizeSale();
					saleFinalized = true;
					break;
				}
			}
		}
		
		// Pass on message to other brokers:
		// K#Publish Finalize Sale#[Broker ID]#Seller[Seller ID]#Buyer[Buyer ID]#[Item ID]#[Final Price]
		String saleFinalizedMsgToBroker = "K#Publish Finalize Sale#" + brokerId + "#Seller" + sellerId + "#Buyer" + buyerId + "#" + itemId + "#" + finalPrice;
		
		// Send message up to parent broker if parent exists
		if (hasParent) {
			sendMsg(saleFinalizedMsgToBroker, parentPort);
		}
		
		// Send item to any other child brokers
		for (int i = 0; i < BrokerServer.childPorts.length; i++) {
			if (BrokerServer.childPorts[i] != -1) {
				// If the port is a broker port and is not the port you received the message from, send message
				if (Arrays.asList(BrokerServer.ALL_BROKER_PORTS).contains(BrokerServer.childPorts[i]) && (!Long.valueOf(BrokerServer.childPorts[i]).equals(Long.valueOf(brokerId)))) {
					sendMsg(saleFinalizedMsgToBroker, BrokerServer.childPorts[i]);
				}
			}
		}
		
		// Send finalized sale notifications to all buyers subscribed:
		// H#Subscribe Item Sold#[Buyer ID]#[Item ID]#[Final Price]
		String saleFinalizedMsgToBuyers = "H#Subscribe Item Sold#" + buyerId + "#" + itemId + "#" + finalPrice;
		
		// Loop through children
		for (int i = 0; i < BrokerServer.childPorts.length; i++) {
			if (BrokerServer.childPorts[i] != -1) {
				// For each child, loop through subscribers to finalized sale
				Iterator<Long> it = currentItem.getSubscribersToSaleFinalized().iterator();
				while (it.hasNext()) {
					// If a child is a subscriber, send notification
					if (Long.valueOf(BrokerServer.childPorts[i]).equals(Long.valueOf(it.next()))) {
						sendMsg(saleFinalizedMsgToBuyers, BrokerServer.childPorts[i]);
					}
				}
				
			}
		}
		
	}

	
	public void subscribeInterest(long buyerId, String name, String attributes,
			float minimumBid) {
		List<Item> matches = matchString(name);
		
		Set<String> attr = new HashSet<String>(
				Arrays.asList(attributes.split(",")));
		for (String s: attr) {
			matches.addAll(matchString(s));
		}
		
		for (Item item: matches) {
			// F#Subscribe Interest#[Item ID]#[name]#[attributes]#[Minimum Bid]
			String interestMsg = "F#Subscribe Interest#" + item.itemId + "#" + name + "#";
			// Add attributes
			String[] attributesArray = item.attributes.toArray(new String[0]);
			if (attributesArray.length > 0) {
				interestMsg += attributesArray[0];
				for (int i = 1; i < attributesArray.length; i++) {
					interestMsg += ',' + attributesArray[i];
				}
			}
			interestMsg += "#" + minimumBid;
			sendMsg(interestMsg, Integer.parseInt(buyerId + ""));
		}
	}

	public void subscribeInterestBidUpdate(long buyerId, long itemId) {
		
		for (Item item: localItems) {
			if (Long.valueOf(itemId).equals(Long.valueOf(item.itemId))) {
				item.addToBidUpdatesSubscription(buyerId);
				return;
			}
		}
		
		for (Item item: importedItems) {
			if (Long.valueOf(itemId).equals(Long.valueOf(item.itemId))) {
				item.addToBidUpdatesSubscription(buyerId);
				return;
			}
		}
	}
	
	private void sendMsg(String message, int port) {
		for (int i = 0; i < BrokerServer.brokerSockets.length; i++) {
			if (BrokerServer.brokerSockets[i].port == port) {
				BrokerServer.brokerSockets[i].out.println(message);
				break;
			}
		}
	}
	
	private List<Item> matchString(String str) {
		
		List<Item> result = new ArrayList<Item>();
		
		for (Item item: localItems) {
			if (item.name.contains(str)) {
				result.add(item);
				break;
			}
			for (String attr: item.attributes) {
				if (attr.contains(str)) result.add(item);
				break;
			}
		}
		
		for (Item item: importedItems) {
			if (item.name.contains(str)) {
				result.add(item);
				break;
			}
			for (String attr: item.attributes) {
				if (attr.contains(str)) result.add(item);
				break;
			}
		}
		
		return result;
	}

}
