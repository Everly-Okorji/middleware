package buyer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import seller.Item;

public class GeneralBuyer implements Buyer {

	long id;
	
	// Keeps track of the items listed by the seller
	List<Item> items = new ArrayList<Item>();
	
	long lastItemId = 0;
	int minID = 0, maxID = 4;
	
	GeneralBuyer(long id) {
		this.id = id;
	}
	
	public long genItemId() {
		// Fetch data from user
		return lastItemId++;
	}

	@Override
	public int publishBid(long itemId, float price) {
		return 0;
	}

	@Override
	public int subscribeInterest(String name, Set<String> attributes,
			float minimumBid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int subscribeInterestBidUpdate(long itemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int subscribeItemSold(long itemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setAutomaticMode(boolean isAuto) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getId() {
		// TODO Auto-generated method stub
		return id;
	}

}
