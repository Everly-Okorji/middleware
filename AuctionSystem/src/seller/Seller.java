package seller;

import java.util.Set;

public interface Seller {

	long getId();
	
	long genItemId();
	
	int publishAvailableItem(long itemId, String name, Set<String> attributes, float minimumBid);
	
	int publishBidUpdate(long itemId);
	
	int publishFinalizeSale(long itemId, float finalPrice, long buyerId);
	
	int subscribeReceiveBid(long itemId);
	
}
