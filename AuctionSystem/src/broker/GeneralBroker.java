package broker;

import java.util.Set;

public class GeneralBroker implements Broker {

	long id;
	
	GeneralBroker() {
		
	}

	public long getId() {
		return id;
	}
	
	@Override
	public int publishAvailableItem(long sellerId, long itemId, String name,
			Set<String> attributes, float minimumBid) {
		// TODO Auto-generated method stub
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
	public int subscribeInterest(long buyerId, String name,
			Set<String> attributes, float minimumBid) {
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
	
}
