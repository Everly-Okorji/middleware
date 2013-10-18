package item;

public interface Item {

	long getId();
	
	float getCurrentBid();
	
	int bid(long buyerId, float price);
	
	int finalizeSale(long sellerId);
	
}
