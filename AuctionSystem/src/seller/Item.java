package seller;

import java.util.Set;

public class Item {
	
	public long itemId;
	public String name;
	public Set<String> attributes;
	public float minimumBid;
	public float currentBid;
	public long buyerId;
	
	long newBidBuyerId;
	float newCurrentBid;
	
	Item(long itemId, String name, Set<String> attributes, float minimumBid) {
		this.itemId = itemId;
		this.name = name;
		this.attributes = attributes;
		this.minimumBid = minimumBid;
		this.currentBid = 0.0f;
	}
	
	boolean confirmBid(long buyerId, float bid) {
		if ((Float.compare(bid, newCurrentBid) == 0) && Long.valueOf(this.buyerId).equals(buyerId)) {
			currentBid = newCurrentBid;
			this.buyerId = newBidBuyerId;
			return true;
		}
		return false;
	}
	
	boolean bid (long buyerId, float bid) {
		if (bid > minimumBid && bid > currentBid) {
			newCurrentBid = bid;
			newBidBuyerId = buyerId;
			return true;
		}
		
		return false;
	}
	
}