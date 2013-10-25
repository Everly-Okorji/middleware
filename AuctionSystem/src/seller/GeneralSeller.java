package seller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GeneralSeller {

	long id;
	List<Item> activeSales, pendingSales;
	PrintWriter toBroker;
	
	
	long lastItemId = 0;
	
	GeneralSeller(long id, PrintWriter out) {
		this.id = id;
		activeSales = new ArrayList<Item>();
		pendingSales = new ArrayList<Item>();
		toBroker = out;
	}
	
	public long generateItemId() {
		return lastItemId++;
	}

	
	public long publishAvailableItem(String name,
			String attributes, float minimumBid) {
		
		// Check valid name and attributes
		if (name.isEmpty()) return -1;
		// Check valid bid
		if (minimumBid <= 0.0) return -2;
		
		Set<String> attr = new HashSet<String>(
				Arrays.asList(attributes.split(",")));
		long itemId=generateItemId();
		
		if (pendingSales.add(new Item(itemId, name, attr, minimumBid))) {
			return itemId;
		} else return -3;
		
	}
	
	public void confirmAddItem(long itemId) {
		Iterator<Item> it = pendingSales.iterator();
		while (it.hasNext()) {
			Item pendingItem = it.next();
			if (Long.valueOf(pendingItem.itemId).equals(Long.valueOf(itemId))) {
				it.remove();
				activeSales.add(pendingItem);
				return;
			} else {
				it.remove();
			}
		}
	}
	
	public void bid(long buyerId, long itemId, float price) {

		// Check if item ID is in list
		int position = -1;
		for (int pos = 0; pos < activeSales.size(); pos++) {
			if (Long.valueOf(itemId).equals(Long.valueOf(activeSales.get(0).itemId))) {
				position = pos;
				break;
			}
		}
		if (position < 0) return; // Item not on list

		if (activeSales.get(0).bid(buyerId, price)) { 
			// Publish Bid Update
			String fromUser = "B#Publish Bid Update#Seller" + id + "#Buyer" + buyerId + "#" + itemId + "#" + price;
			SellerClient.out.println(fromUser);
		} else return; // Price too low
	
	}
	
	public void confirmBidUpdate(long buyerId, long itemId, float price) {
		
		Iterator<Item> it = activeSales.iterator();
		while (it.hasNext()) {
			Item item = it.next();
			if (Long.valueOf(item.itemId).equals(Long.valueOf(itemId))) {
				item.confirmBid(buyerId, price);
			}
		}
		
	}
	
	public void finalizeSale(long buyerId, long itemId, float price) {
		Iterator<Item> it = activeSales.iterator();
		while (it.hasNext()) {
			if (Long.valueOf(it.next().itemId).equals(Long.valueOf(itemId)))
				it.remove();
		}
	}

	public long getId() {
		return id;
	}


}
