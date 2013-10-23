package seller;

public class Main {

	public static void main(String[] args) {
		SellerClient client = new SellerClient();
		if (client.getId()) client.run();
	}

}
