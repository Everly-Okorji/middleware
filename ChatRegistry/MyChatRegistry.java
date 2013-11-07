import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the implementation of the ChatRegistry interface.
 * @author Everly, Zirui
 *
 */
public class MyChatRegistry implements ChatRegistry {

	// List of all registered chat rooms and clients
	List<ChatRoomServer> rooms;
	List<ChatClient> clients;
	
	MyChatRegistry() {
		rooms = new ArrayList<ChatRoomServer>();
		clients = new ArrayList<ChatClient>();
	}
	
	
	// ---------------- PRIVATE METHODS -----------------
	
	/**
	 * This function checks if a given name is available for use (i.e. not taken)
	 * @param room_name
	 * @return true if room name is vacant, false otherwise
	 * @throws RemoteException 
	 */
	private boolean roomNameAvailable(String room_name) throws RemoteException {
		
		for (ChatRoomServer room: rooms) {
			if (room.getName().equals(room_name)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * This function checks to see if a client name is available to be used
	 * @param client_name
	 * @return true if client name is vacant, false otherwise
	 * @throws RemoteException 
	 */
	private boolean clientNameAvailable(String client_name) throws RemoteException {
		
		for (ChatClient client: clients) {
			if (client.getName().equals(client_name)) {
				return false;
			}
		}
		return true;
	}
	
	
	//-------------- PUBLIC METHODS ---------------------

	@Override
	public int register(Object entity, Type entityType) throws RemoteException {
		switch (entityType) {

		case CHATROOM:
			ChatRoomServer room = (ChatRoomServer) entity;
			// If name is available, add name to name list
			if (this.roomNameAvailable(room.getName())) {
				this.rooms.add(room);
				return 0;
			}
			System.err.println("A chat room with name '" + room.getName() + "' already exists!");
			return 1;

		case CHATCLIENT:
			ChatClient client = (ChatClient) entity;
			// If name is available, add client to list
			if (this.clientNameAvailable(client.getName())) {
				this.clients.add(client);
				return 0;
			}
			System.err.println("A client with name '" + client.getName() + "' already exists!");
			return 1;

		default:
			System.err.println("Invalid Enum Type.");
			return -1;
		}
	}
	
	@Override
	public int deregister(String entityName, Type entityType) throws RemoteException {
		
		switch (entityType) {

		case CHATROOM:
			String room_name = entityName;
			// If name exists in the list, remove object from list
			if (!roomNameAvailable(room_name)) {
				ChatRoomServer room = null;
				// Find the room which the provider wants to deregister
				for (ChatRoomServer chatroom: rooms) {
					if (chatroom.getName().equals(room_name)) {
						room = chatroom;
						break;
					}
				}
				if (rooms.remove(room)) {
					return 0;
				}
				else {
					System.err.println("Attempted to deregister chat room '" + room_name + "', but remove operation failed!");
					return 2;
				}
			}
			System.err.println("Attempted to deregister chat room '" + room_name + "', but chat room doesn't exist!");
			return 1;

		case CHATCLIENT:
			String client_name = entityName;
			// If name is in client list, fetch object and remove from list
			if (!clientNameAvailable(client_name)) {
				ChatClient client = null;
				
				// Find the client object to be deregistered
				for (ChatClient c: clients) {
					if (c.getName().equals(client_name)) {
						client = c;
						break;
					}
				}
				if (clients.remove(client)) {
					return 0;
				} else {
					System.err.println("Attempted to deregister client '" + client_name + "', but remove operation failed!");
					return 2;
				}
			}
			System.err.println("Attempted to deregister client '" + client_name + "', but this client doesn't exist!");
			return 1;

		default:
			System.err.println("Invalid Enum Type.");
			return -1;

		}

	}

	@Override
	public Object getInfo(String entityName, Type entityType) throws RemoteException {
	
		switch (entityType) {
 
		case CHATROOM:
			for (ChatRoomServer c: rooms){
				if (c.getName().equals(entityName)){
					return c;
				}
			}
			System.err.println("Cannot find info of chat room '"+entityName+"'");
			return null;
			
		case CHATCLIENT:
			for (ChatClient c: clients){
				if (c.getName().equals(entityName)){
					return c;
				}
			}
			System.err.println("Cannot find info of chat client '"+entityName+"'");
			return null;

		default:
			System.err.println("Invalid Enum Type.");
			return null;
		}
	}

	@Override
	public List<String> getClientsList() throws RemoteException {
		List<String> clientNames=new ArrayList<String>();
		for (ChatClient client: clients){
			clientNames.add(client.getName());
		}
		return clientNames;
	}

	@Override
	public List<String> getChatRoomsList() throws RemoteException {
		List<String> chatRoomList=new ArrayList<String>();
		for (ChatRoomServer chatroom: rooms){
			chatRoomList.add(chatroom.getName());
		}
		return chatRoomList;
	}

}
