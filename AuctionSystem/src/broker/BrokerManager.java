package broker;

public class BrokerManager {
	
	static Broker[] brokers;

	public static void main(String[] args) {
		
		brokers = new GeneralBroker[6];
		
		for (int i = 0; i < brokers.length; i++) {
			brokers[i] = new GeneralBroker();
		}
		
		// Set children for the HEAD broker
		brokers[0].setChildBroker(brokers[1].getId());
		brokers[0].setChildBroker(brokers[2].getId());
		
		// Set children for the broker1
		brokers[1].setChildBroker(brokers[3].getId());
		brokers[1].setChildBroker(brokers[4].getId());
		
		// Set children for the broker2
		brokers[2].setChildBroker(brokers[5].getId());
		
		

	}

}
