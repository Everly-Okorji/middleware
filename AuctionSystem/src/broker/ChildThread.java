package broker;

public class ChildThread extends Thread {

	int index;
	BrokerSocket b;
	
	ChildThread(BrokerSocket b, int i) {
		this.index = i;
		this.b = b;
	}
	
	@Override
	public void run() {
		b.accept();
		b.run();
	}
}
