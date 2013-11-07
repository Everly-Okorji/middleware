import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ChatRoomProvider extends Remote {

	/**
	 * This function is used to open a new chat room. It can be called at any
	 * time during the session.
	 * @param roomName The name of the room to open
	 * @return 0 if successful, otherwise it returns the error code of ChatRegistry's register method
	 * @throws RemoteException
	 */
	int openChatRoom (String roomName) throws RemoteException;
	
	/**
	 * This function is used to close an existing chat room. The function
	 * is successful ONLY if all chat clients have left the room.
	 * @param roomName
	 * @return 0 if the room is successfully removed, otherwise it returns the
	 * error code of the ChatRegistry's deregister method
	 * @throws RemoteException
	 */
	int closeChatRoom (String roomName) throws RemoteException;
	
}
