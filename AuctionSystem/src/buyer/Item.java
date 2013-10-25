package buyer;

import java.util.Set;

public class Item {
	
	public long itemId;
	public String name;
	public Set<String> attributes;
	public float minimumBid;
	public float currentBid;
	public long buyerId;
	
	boolean saleFinalized;
	
	long newBidBuyerId;
	float newCurrentBid;
	
	Item(long itemId, String name, Set<String> attributes, float minimumBid) {
		this.itemId = itemId;
		this.name = name;
		this.attributes = attributes;
		this.minimumBid = minimumBid;
		this.currentBid = 0.0f;
	}
	
	void confirmBid(long buyerId, float bid) {
		if ((Float.compare(bid, newCurrentBid) == 0) && Long.valueOf(this.buyerId).equals(buyerId)) {
			currentBid = newCurrentBid;
			this.buyerId = newBidBuyerId;
		}
	}
	
	void bid (long buyerId, float bid) {
		if (bid > minimumBid && bid > currentBid) {
			newCurrentBid = bid;
			newBidBuyerId = buyerId;
		}
	}
	
	void confirmOtherBuyerBid(long buyerId, float bid) {
		this.buyerId = buyerId;
		this.currentBid = bid;
	}
	
	void confirmSale (long buyerId, long itemId, float finalPrice) {
		if ((Float.compare(finalPrice, currentBid) == 0) && Long.valueOf(this.buyerId).equals(buyerId)) {
			saleFinalized = true;
		}
	}
	
	boolean saleFinalized() {
		return saleFinalized;
	}
	
}