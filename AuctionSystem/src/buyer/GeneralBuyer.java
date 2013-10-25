package buyer;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneralBuyer {

	long id;
	PrintWriter toBroker;
	List<Item> itemsUnderInterest;	// Results of an interest from buyer is added to this list
	// Keeps track of the items under bid by the buyer
	List<Long> itemsUnderBid;
	
	boolean isAutomaticMode;
	float maximumBid;
	float increment;
	
	
	public GeneralBuyer(int userId, PrintWriter out) {
		this.id = userId;
		this.toBroker = out;
		this.itemsUnderInterest = new ArrayList<Item>();
		this.itemsUnderBid = new ArrayList<Long>();
		this.isAutomaticMode = false;
		this.maximumBid = 0.0f;
		this.increment = 0.0f;
	}

	public void processInterest(long itemId, String name, String attributes, float minimumBid) {

		Set<String> attr = new HashSet<String>(
				Arrays.asList(attributes.split(",")));
		Item item = new Item(itemId, name, attr, minimumBid);
		if (isAutomaticMode) {
			item.bid(id, minimumBid + increment);
			this.itemsUnderBid.add(itemId);
		}
		this.itemsUnderInterest.add(item);
		
	}
	
	public void bid(long itemId, float price) {
		for (Item item: itemsUnderInterest) {
			if (Long.valueOf(item.itemId).equals(Long.valueOf(itemId))) {
				item.bid(id, price);
				break;
			}
		}
		
		// Publish bid
		String bidMsg = "E#Publish Bid#Buyer" + id + "#" + itemId + "#" + price;
		toBroker.println(bidMsg);
	}
	
	public void confirmBid(long buyerId, long itemId, float price) {
		for (Item item: itemsUnderInterest) {
			if (Long.valueOf(item.itemId).equals(Long.valueOf(itemId))) {
				item.confirmBid(id, price);
				break;
			}
		}
		itemsUnderBid.add(itemId);
	}

	public void processBidUpdate(long buyerId, long itemId, float price) {
		for (Item item: itemsUnderInterest) {
			if (Long.valueOf(item.itemId).equals(Long.valueOf(itemId))) {
				item.confirmOtherBuyerBid(id, price);
				if (isAutomaticMode) {
					if ((price + increment) < maximumBid) {
						bid(id, price + increment);
					}
				}
				break;
			}
		}
	}

	public void processItemSold(long buyerId, long itemId, float finalPrice) {
		for (Item item: itemsUnderInterest) {
			if (Long.valueOf(item.itemId).equals(Long.valueOf(itemId))) {
				item.confirmSale(buyerId, itemId, finalPrice);
				break;
			}
		}
		itemsUnderBid.remove(itemId);
	}
	
	public void setAutomaticMode(float maximumBid, float increment) {
		isAutomaticMode = true;
		this.maximumBid = maximumBid;
		this.increment = 0;
	}
	
	public void setUserDrivenMode() {
		this.isAutomaticMode = false;
		this.maximumBid = 0.0f;
		this.increment = 0.0f;
	}

	public long getId() {
		return id;
	}

}
