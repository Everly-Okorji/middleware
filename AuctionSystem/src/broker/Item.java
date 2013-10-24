package broker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
	
	private List<Long> bidUpdatesSubFromBuyers;
	private List<Long> saleSubFromBuyers;
	
	Item(long itemId, String name, Set<String> attributes, float minimumBid) {
		this.itemId = itemId;
		this.name = name;
		this.attributes = attributes;
		this.minimumBid = minimumBid;
		this.currentBid = 0.0f;
		
		bidUpdatesSubFromBuyers = new ArrayList<Long>();
		saleSubFromBuyers = new ArrayList<Long>();
	}
	
	void addToBidUpdatesSubscription (long buyerId) {
		bidUpdatesSubFromBuyers.add(buyerId);
	}
	
	void addToSaleSubscription (long buyerId) {
		saleSubFromBuyers.add(buyerId);
	}
	
	boolean isSubscribedToBidUpdates(long buyerId) {
		
		Iterator<Long> it = bidUpdatesSubFromBuyers.iterator();
		while (it.hasNext()) {
			if (Long.valueOf(it.next()).equals(Long.valueOf(buyerId))) {
				return true;
			}
		}
		return false;
	}
	
	boolean isSubscribedToSale(long buyerId) {
		
		Iterator<Long> it = saleSubFromBuyers.iterator();
		while (it.hasNext()) {
			if (Long.valueOf(it.next()).equals(Long.valueOf(buyerId))) {
				return true;
			}
		}
		return false;
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