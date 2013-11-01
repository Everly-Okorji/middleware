package server;

public class MyChatRoomServer implements ChatRoomServer {
	
	String name;
	
	public MyChatRoomServer(String name) {
		this.name = name;
	}

	@Override
	public int join(String clientName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int talk(String clientName, String message) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int leave(String clientName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

}
