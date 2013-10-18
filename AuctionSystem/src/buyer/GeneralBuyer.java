package buyer;

import java.util.Set;

public class GeneralBuyer implements Buyer {

	long id;
	
	public GeneralBuyer() {
		
	}

	@Override
	public int publishBid(long itemId, float price) {
		// TODO Auto-generated method stub
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
		return 0;
	}

}
