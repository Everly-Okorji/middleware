package server;
import java.rmi.Remote;


public interface ChatRoomServer extends Remote {

	int join(ChatClient client);
	
	int talk(String clientName, String message);
	
	int leave(String clientName);
	
	String getName();
	
}
