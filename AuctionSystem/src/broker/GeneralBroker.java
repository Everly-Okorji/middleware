package broker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GeneralBroker implements Broker {

	long id;
	List<Item> itemsHolds = new ArrayList<Item>();

	GeneralBroker() {

	}

	public long getId() {
		return id;
	}

	@Override
	public int publishAvailableItem(long sellerId, long itemId, String name,
			String attributes, float minimumBid) {
		
		Set<String> attr= new HashSet<String>(
				Arrays.asList(attributes.split(",")));
		
		itemsHolds.add(new Item(sellerId, itemId, name, attr, minimumBid));
		return 0;
	}

	@Override
	public int publishBid(long buyerId, long itemId, float price) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int publishBidUpdate(long sellerId, long itemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int publishFinalizeSale(long sellerId, long itemId,
			float finalPrice, long buyerId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int subscribeInterest(long buyerId, String name, String attributes,
			float minimumBid) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int subscribeReceiveBid(long sellerId, long itemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int subscribeInterestBidUpdate(long buyerId, long itemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int subscribeItemSold(long buyerId, long itemId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int setChildBroker(long brokerId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Broker getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String genSellerId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String genBuyerId() {
		// TODO Auto-generated method stub
		return null;
	}

}
