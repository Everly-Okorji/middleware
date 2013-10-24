import seller.SellerClient;
import buyer.BuyerClient;
import broker.BrokerServer;


public class Main {

	public static void main(String[] args) {

		new Thread(new Runnable() {
			public void run() {
				new BrokerServer("localhost", 2000);
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				seller.Main.main(new String[0]);
			}
		}).start();
	}

}
