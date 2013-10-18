package seller;

import java.util.Set;

public class GeneralSeller implements Seller {

	long id;
	
	GeneralSeller() {
		
	}
	
	@Override
	public int publishAvailableItem(long itemId, String name,
			Set<String> attributes, float minimumBid) {
		// TODO Auto-generated method stub
		return 0;
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
