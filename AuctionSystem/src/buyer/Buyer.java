package buyer;

import java.util.Set;

public interface Buyer {
	
	long getId();

	int publishBid(long itemId, float price);
	
	int subscribeInterest(String name, Set<String> attributes, float minimumBid);
	
	int subscribeInterestBidUpdate(long itemId);
	
	int subscribeItemSold(long itemId);
	
	int setAutomaticMode(boolean isAuto);
	
}
