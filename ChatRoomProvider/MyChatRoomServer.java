

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class MyChatRoomServer extends UnicastRemoteObject implements ChatRoomServer {
	
	String name;
	List<ChatClient> clients;
	
	
	public MyChatRoomServer(String name) throws RemoteException {
		this.name = name;
		this.clients = new ArrayList<ChatClient>();
	}

	@Override
	public int join(ChatClient client) {
		
		// Check if client already exists
		if (isExistingClient(client.getName())) {
			System.out.println("Client name '" + client.getName() + "' is an existing member!");
			return 1;
		}
		// Add client to list
		clients.add(client);
		return 0;
	}

	@Override
	public int talk(String clientName, String message) {
		
		// Check if the sender of the message is not a joined client
		if (!isExistingClient(clientName)) {
			System.err.println("Client '" + clientName + "' is not a joined chat client!");
			return 1;
		}
		
		// Send message to all clients
		for (ChatClient client: clients) {
			client.receiveMessage(name, clientName + ": " + message);
		}
		return 0;
	}

	@Override
	public int leave(String clientName) {
		
		// Check if client is not a joined client
		if (!isExistingClient(clientName)) {
			System.err.println("Client '" + clientName + "' is not a joined chat client!");
			return 1;
		}
		
		// Remove client from list
		for (ChatClient client: clients) {
			if (client.getName().equals(clientName)) {
				clients.remove(client);
				return 0;
			}
		}
		return 2;
	}

	@Override
	public String getName() {
		return name;
	}
	
	private boolean isExistingClient(String new_client) {
		
		for (ChatClient client: clients) {
			// If they have the same name, they are the same client
			if (client.getName().equals(new_client)) {
				return true;
			}
		}
		return false;
		
	}

}
