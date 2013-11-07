import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface ChatRegistry extends Remote {

	enum Type {CHATROOM, CHATCLIENT};
	
	/**
	 * This function determines whether the entity is a ChatClient object or a
	 * ChatRoomServer object, and then adds the object to the appropriate list.
	 * @param entity The ChatClient OR ChatRoomServer object to be added
	 * @param entityType The enum type Type.CHATROOM or Type.CHATCLIENT
	 * @return 0 if the object was successfully registered, 1 if the object
	 * already exists, or -1 if the enum type specified is invalid.
	 * @throws RemoteException
	 */
	int register(Object entity, Type entityType) throws RemoteException;
	
	/**
	 * This function is used to deregister a chat client or chat room by searching
	 * for the object based on the type specified, before removing it from the list.
	 * @param entityName The name of the object to be removed
	 * @param entityType The enum type Type.CHATROOM or Type.CHATCLIENT
	 * @return 0 if process was successful, 1 if no object of the specified type
	 * exists with the specified name, 2 if the remove operation fails, -1 if an
	 * invalid enum type is specified
	 * @throws RemoteException
	 */
	int deregister(String entityName, Type entityType) throws RemoteException;
	
	/**
	 * This function looks for a specified object stub in the registry and returns the
	 * stub to the requester.
	 * @param entityName The name of the stub being requested
	 * @param entityType The enum type Type.CHATROOM or Type.CHATCLIENT
	 * @return the requested object (i.e. ChatClient object or ChatRoomServer object)
	 * if successful, or null if an error occurs during the process.
	 * @throws RemoteException
	 */
	Object getInfo(String entityName, Type entityType) throws RemoteException;
	
	/**
	 * This function is used to fetch a list of all clients registered in the chat
	 * registry and not yet deregistered.
	 * @return A list of registered ChatClient objects.
	 * @throws RemoteException
	 */
	List<String> getClientsList() throws RemoteException;
	
	/**
	 * This function is used to fetch a list of all chat rooms that have been
	 * registered by a chat room provider and not since removed.
	 * @return A list of registered ChatRoomServer objects
	 * @throws RemoteException
	 */
	List<String> getChatRoomsList() throws RemoteException;
	
}
