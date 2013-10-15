import java.io.IOException;

import client.KnockKnockClient;
import server.KnockKnockServer;


public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		new Thread(new Runnable() {
			public void run() {
				try {
					KnockKnockServer.run();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		new Thread(new Runnable() {
			public void run() {
				try {
					KnockKnockClient.run();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		//KnockKnockClient.run(args);
	}

}
