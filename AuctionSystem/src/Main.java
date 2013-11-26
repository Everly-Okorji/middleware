import java.io.IOException;

import seller.SellerClient;
import buyer.BuyerClient;
import broker.BrokerServer;


public class Main {

	public static void main(String[] args) {

		new Thread(new Runnable() {
			public void run() {
				try {
					superbroker.SuperBroker.main(new String[0]);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		
		for (int i = 0; i < 6; i++) {
			new Thread(new Runnable() {
				public void run() {
					new BrokerServer("localhost", 2000);
				}
			}).start();
		}
		
		new Thread(new Runnable() {
			public void run() {
				seller.Main.main(new String[0]);
			}
		}).start();
	}

}
