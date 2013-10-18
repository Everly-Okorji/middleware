package broker;

import java.util.Set;

public interface Broker {
	
	long getId();
	
	int publishAvailableItem(long sellerId, long itemId, String name, Set<String> attributes, float minimumBid);

	int publishBid(long buyerId, long itemId, float price);
	
	int publishBidUpdate(long sellerId, long itemId);
	
	int publishFinalizeSale(long sellerId, long itemId, float finalPrice, long buyerId);
	
	int subscribeInterest(long buyerId, String name, Set<String> attributes, float minimumBid);
	
	int subscribeReceiveBid(long sellerId, long itemId);
	
	int subscribeInterestBidUpdate(long buyerId, long itemId);
	
	int subscribeItemSold(long buyerId, long itemId);
	
	int setChildBroker(long brokerId);
	
	Broker getParent();
	
}
