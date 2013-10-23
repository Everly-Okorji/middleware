package broker;

public class Main {

	public static void main(String[] args) {
		
		BrokerServer brokerServer = new BrokerServer("localhost", 2000);
		brokerServer.run();
	}

}
