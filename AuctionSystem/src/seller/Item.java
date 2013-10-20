package seller;

import java.util.Set;

public class Item {
	
	public long itemId;
	public String name;
	public Set<String> attributes;
	public float minimumBid;
	public float currentBid;
	public long buyerId;
	
	Item(long itemId, String name, Set<String> attributes, float minimumBid) {
		this.itemId = itemId;
		this.name = name;
		this.attributes = attributes;
		this.minimumBid = minimumBid;
		this.currentBid = 0.0f;
	}
	
	boolean bid (long buyerId, float bid) {
		if (bid > minimumBid && bid > currentBid) {
			currentBid = bid;
			this.buyerId = buyerId;
			return true;
		}
		
		return false;
	}
	
}