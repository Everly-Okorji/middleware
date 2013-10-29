import java.rmi.Remote;
import java.util.List;


public interface ChatClient extends Remote {

	List<String> findAvailableChatRooms();
	
	int joinChatRoom(String name);
	
	int leaveChatRoom(String name);
	
	int sendMessage(String room, String message);
	
	int quit();
	
}
