package seller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GeneralSeller implements Seller {

	long id;
	
	// Keeps track of the items listed by the seller
	List<Item> items = new ArrayList<Item>();
	
	long lastItemId = 0;
	int minID = 0, maxID = 4;
	
	GeneralSeller(long id) {
		this.id = id;
	}
	
	public long genItemId() {
		// Fetch data from user
		return lastItemId++;
	}
	
	@Override
	public int publishAvailableItem(long itemId, String name,
			Set<String> attributes, float minimumBid) {
		
		// Check valid name and attributes
		if (name.isEmpty()) return 2;
		
		// Check valid bid
		if (minimumBid <= 0.0) return 3;
		
		if (items.add(new Item(itemId, name, attributes, minimumBid))) {
			return 0;
		} else return 4;
	}
	
	public boolean bid(long itemId, float price) {

		// Check if item ID is in list
		int position = -1;
		for (int pos = 0; pos < items.size(); pos++) {
			if (Long.valueOf(itemId).equals(Long.valueOf(items.get(0).itemId))) {
				position = pos;
				break;
			}
		}
		if (position < 0) return false; // Item not on list
		
		if (items.get(0).bid(id, price)) { 
			return true;
		} else return false; // Price too low
	}

	@Override
	public int publishBidUpdate(long itemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int publishFinalizeSale(long itemId, float finalPrice, long buyerId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int subscribeReceiveBid(long itemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
