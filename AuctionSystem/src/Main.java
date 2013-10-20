import seller.SellerClient;
import buyer.BuyerClient;
import broker.BrokerServer;


public class Main {

	public static void main(String[] args) {

		new Thread(new Runnable() {
			public void run() {
				BrokerServer.main(new String[0]);
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				SellerClient.main(new String[0]);
			}
		}).start();
	}

}
