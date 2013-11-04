import java.util.ArrayList;
import java.util.List;


public class MyChatRegistry implements ChatRegistry {

	List<String> roomNames;
	List<String> clientNames;
	
	MyChatRegistry() {
		roomNames = new ArrayList<String>();
		clientNames = new ArrayList<String>();
	}
	
	
	// ---------------- PRIVATE METHODS -----------------
	
	/**
	 * @author Everly
	 * @param room_name
	 * @return true if room name is vacant, false otherwise
	 */
	private boolean roomNameAvailable(String room_name) {
		
		for (String name: roomNames) {
			if (name.equals(room_name)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @author Everly
	 * @param client_name
	 * @return true if client name is vacant, false otherwise
	 */
	private boolean clientNameAvailable(String client_name) {
		
		for (String name: clientNames) {
			if (name.equals(client_name)) {
				return false;
			}
		}
		return true;
	}
	
	
	//-------------- PUBLIC METHODS ---------------------
	
	/**
	 * @author Everly
	 */
	@Override
	public int register(String entityName, Type entityType) {
		switch (entityType) {

		case CHATROOM:
			String room_name = entityName;
			// If name is available, add name to name list
			if (roomNameAvailable(room_name)) {
				roomNames.add(room_name);
				return 0;
			}
			System.err.println("A chat room with name '" + room_name + "' already exists!");
			return 1;

		case CHATCLIENT:
			String client_name = entityName;
			// If name is available, add client to list
			if (clientNameAvailable(client_name)) {
				clientNames.add(client_name);
				return 0;
			}
			System.err.println("A client with name '" + client_name + "' already exists!");
			return 1;

		default:
			System.err.println("Invalid Enum Type.");
			return -1;
		}
	}

	@Override
	public int deregister(String entityName, Type entityType) {
		
		switch (entityType) {

		case CHATROOM:
			String room_name = entityName;
			// If name is available, add name to name list
			if (!roomNameAvailable(room_name)) {
				roomNames.remove(room_name);
				return 0;
			}
			System.err.println("Attempted to deregister '" + room_name + "', but chat room doesn't exist!");
			return 1;

		case CHATCLIENT:
			String client_name = entityName;
			// If name is available, add client to list
			if (!clientNameAvailable(client_name)) {
				clientNames.remove(client_name);
				return 0;
			}
			System.err.println("Attempted to deregister '" + client_name + "', but this client doesn't exist!");
			return 1;

		default:
			System.err.println("Invalid Enum Type.");
			return -1;

		}

	}

	@Override
	public String getInfo(String entityName) {
		// TODO Auto-generated method stub
		return null;
	}

}
