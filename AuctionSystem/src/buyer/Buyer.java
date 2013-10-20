package buyer;

import java.util.Set;

public interface Buyer {
	
	long getId();
	
	int setAutomaticMode(boolean isAuto);

	int publishBid(long itemId, float price);
	
	int subscribeInterest(String name, Set<String> attributes, float minimumBid);
	
	int subscribeInterestBidUpdate(long itemId);
	
	int subscribeItemSold(long itemId);
	
}
