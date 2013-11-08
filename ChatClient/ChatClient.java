import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public interface ChatClient extends Remote {
	
	/**
	 * This method gets the client's name and returns it.
	 * @return A string containing the client's name
	 * @throws RemoteException
	 */
	String getName() throws RemoteException;
	
	/**
	 * This method finds the current available chat rooms registered on ChatRegistry.
	 * and return the List to the client.
	 * @return A string list containing the current registered available chat rooms.
	 * @throws RemoteException
	 */
	List<String> findAvailableChatRooms() throws RemoteException;
	
	/**
	 * This method registers the client with the ChatRegistry.
	 * @return 0 if successful, 1 if failed.
	 * @throws RemoteException
	 */
	int regChatClient() throws RemoteException;
	
	/**
	 * This method adds the client to an exist chat room.
	 * @param name The name of the chat room the client want to join in.
	 * @return 0 if successful, 1 if there does not exist such a chat room.
	 * @throws RemoteException
	 */
	int joinChatRoom(String name) throws RemoteException;
	
	/**
	 * This method removes the client from an exist chat room which the client has joined in before.
	 * @param name The name of the chat room the client want to leave from.
	 * @return 0 successful, 1 failed, 2 if the client did not join the chat room.
	 * @throws RemoteException
	 */
	int leaveChatRoom(String name) throws RemoteException;
	
	/**
	 * This method sends the message the client typed in to the chat room the client is currently chatting in.
	 * @param room The name of the chat room the client is currently chatting in
	 * @param message The message the client want to send
	 * @return 0 if successful, 1 if failed, 2 if the client did not join the chat room specified
	 * @throws RemoteException
	 */
	int sendMessage(String room, String message) throws RemoteException;
	
	/**
	 * This method receives the messages from any chat rooms the client joined in.
	 * It should be invoked by the ChatRoomServer.
	 * @param room The name of chat room where the message is received from
	 * @param message The message received
	 * @return always return 0, other return types reserved for future modification if needed
	 * @throws RemoteException
	 */
	int receiveMessage(String room, String message) throws RemoteException;
	
	/**
	 * This method removes the client from all the chat rooms joined in, 
	 * then de-register the client from the ChatRegistry
	 * and finally shut down this session.
	 * @throws RemoteException
	 */
	void quit() throws RemoteException;

	/**
	 * This method checks if the client has joined one specific chat room.
	 * @param roomname The name of the chat room
	 * @return 0 if the client joined is chat room, 1 if not
	 * @throws RemoteException
	 */
	int checkJoinedRoom(String roomname) throws RemoteException;
	
	/**
	 * This method prints out all the chat rooms the client has joined.
	 * @throws RemoteException
	 */
	void printJoinedRooms() throws RemoteException;
	
}
