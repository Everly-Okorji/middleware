package buyer;

import java.util.Set;

public class Item {
	
	public long itemId;
	public String name;
	public Set<String> attributes;
	public float minimumBid;
	
	Item(long itemId, String name, Set<String> attributes, float minimumBid) {
		this.itemId = itemId;
		this.name = name;
		this.attributes = attributes;
		this.minimumBid = minimumBid;
	}
	
}