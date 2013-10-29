import java.rmi.Remote;

public interface ChatRoomProvider extends Remote {

	int openChatRoom (String roomName);
	
	int closeChatRoom (String roomName);
	
}
