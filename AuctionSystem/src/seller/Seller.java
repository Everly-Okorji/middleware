package seller;

public interface Seller {

	long getId();
	
	long genItemId();
	
	int removeItem(long itemId);
	
	int publishAvailableItem(long itemId, String name, String attributes, float minimumBid);
	
	int publishBidUpdate(long itemId);
	
	int publishFinalizeSale(long itemId, float finalPrice, long buyerId);
	
	int subscribeReceiveBid(long itemId);
	
}
