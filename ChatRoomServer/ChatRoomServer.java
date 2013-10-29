import java.rmi.Remote;


public interface ChatRoomServer extends Remote {

	int join(String clientName);
	
	int talk(String clientName, String message);
	
	int leave(String clientName);
	
}
