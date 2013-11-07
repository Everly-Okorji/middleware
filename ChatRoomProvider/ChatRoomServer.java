
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ChatRoomServer extends Remote {

	/**
	 * This function is used to add a client to a chat room. The client object is added
	 * to the list of clients. Client names are unique.
	 * @param client The ChatClient object for the client wishing to be added to the chat room
	 * @return 0 if the add was successful, or 1 if the client is already a member of the room
	 * @throws RemoteException
	 */
	int join(ChatClient client) throws RemoteException;
	
	/**
	 * This function is used to send a message to all current members of the chat room.
	 * @param clientName The name of the client who sends the message
	 * @param message The message being sent to the chat room
	 * @return 1 if the client sending the message is not a member of the room
	 * @throws RemoteException
	 */
	int talk(String clientName, String message) throws RemoteException;
	
	/**
	 * This function is called by a client who wished to leave the room. The client
	 * object is deleted from the list.
	 * @param clientName The name of the client who wishes to be removed from the chat room
	 * @return 0 if the client was successfully removed, 1 if the client is not a current room member
	 * @throws RemoteException
	 */
	int leave(String clientName) throws RemoteException;
	
	/**
	 * This function is used to check if there is at least one client in the room
	 * @return true if there is at least one client in the room, false if room is empty
	 * @throws RemoteException
	 */
	boolean hasClient() throws RemoteException;
	
	/**
	 * This function is used to fetch the name of the chat room
	 * @return the name of the chat room
	 * @throws RemoteException
	 */
	String getName() throws RemoteException;
	
}
