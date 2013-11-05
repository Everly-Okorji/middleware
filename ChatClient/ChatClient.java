import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public interface ChatClient extends Remote {
	
	BlockingQueue<String> messageQueue = null;
	
	String getName() throws RemoteException;
	
	List<String> findAvailableChatRooms() throws RemoteException;
	
	int regChatClient(String name, ChatRegistry c) throws RemoteException;
	
	int joinChatRoom(String name) throws RemoteException;
	
	int leaveChatRoom(String name) throws RemoteException;
	
	int sendMessage(String room, String message) throws RemoteException;
	
	int receiveMessage(String room, String message) throws RemoteException;
	
	void quit() throws RemoteException;
	
}
