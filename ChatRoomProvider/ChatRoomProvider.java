import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatRoomProvider extends Remote {

	int openChatRoom (String roomName) throws RemoteException;
	
	int closeChatRoom (String roomName) throws RemoteException;
	
}
