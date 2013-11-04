import java.rmi.Remote;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public interface ChatClient extends Remote {
	BlockingQueue<String> messageQueue=null;
	List<String> findAvailableChatRooms();
	
	int regChatClient(String name, ChatRegistry c);
	
	int joinChatRoom(String name);
	
	int leaveChatRoom(String name);
	
	int sendMessage(String room, String message);
	
	int receiveMessage(String room, String message);
	
	void quit();
	
}
