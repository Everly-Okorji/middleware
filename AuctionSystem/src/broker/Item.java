package broker;

import java.util.Set;

public class Item {
	
	public long itemId;
	public String name;
	public Set<String> attributes;
	public float minimumBid;
	public long sellerId;
	public long buyerId;
	
	Item(long sellerId, long itemId, String name, Set<String> attributes, float minimumBid) {
		this.sellerId=sellerId;
		this.itemId = itemId;
		this.name = name;
		this.attributes = attributes;
		this.minimumBid = minimumBid;
	}
	
}