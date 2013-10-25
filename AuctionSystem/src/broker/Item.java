package broker;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Item {
	
	public long itemId;
	public String name;
	public Set<String> attributes;
	public float minimumBid;
	public float currentBid;
	public long buyerId;
	public long sellerId;
	boolean saleFinalized;
	
	private Set<Long> bidUpdatesSubFromBuyers;
	private Set<Long> saleSubFromBuyers;
	
	Item(long sellerId, long itemId, String name, Set<String> attributes, float minimumBid) {
		this.sellerId = sellerId;
		this.itemId = itemId;
		this.name = name;
		this.attributes = attributes;
		this.minimumBid = minimumBid;
		this.currentBid = 0.0f;
		saleFinalized = false;
		
		bidUpdatesSubFromBuyers = new HashSet<Long>();
		saleSubFromBuyers = new HashSet<Long>();
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
	
	Set<Long> getSubscribersToBidUpdate() {
		return bidUpdatesSubFromBuyers;
	}
	
	Set<Long> getSubscribersToSaleFinalized() {
		return saleSubFromBuyers;
	}
	
	void finalizeSale() {
		saleFinalized = true;
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
	
	boolean makeBid (long buyerId, float bid) {
		if (bid > minimumBid && bid > currentBid) {
			currentBid = bid;
			this.buyerId = buyerId;
			return true;
		}
		
		return false;
	}
	
}