import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

/**
 * This is the implementation of the chat room provider requirement in the 
 * specifications. This is where a chat room is opened or closed.
 * @author Everly Okorji
 *
 */

public class MyChatRoomProvider implements ChatRoomProvider {
	
	ChatRegistry registry;
	List<ChatRoomServer> chatRooms;
	
	MyChatRoomProvider(ChatRegistry registry) {
		this.registry = registry;
		this.chatRooms = new ArrayList<ChatRoomServer>();
	}
	
	@Override
	public int openChatRoom(String room_name) throws RemoteException {
		ChatRoomServer room = new MyChatRoomServer(room_name);
		int result = registry.register(room, ChatRegistry.Type.CHATROOM);
		if (result == 0) {
			chatRooms.add(room);
			return 0;
		}
		System.err.println("Could not open chat room with name '" + room_name + "'. Error code was " + result);
		return result;
	}

	@Override
	public int closeChatRoom(String room_name) throws RemoteException {
		ChatRoomServer room = null;
		for (ChatRoomServer chatroom: chatRooms) {
			if (chatroom.getName().equals(room_name)) {
				room = chatroom;
				break;
			}
		}
		if (room.hasClient()) return 10;
		
		int result = registry.deregister(room_name, ChatRegistry.Type.CHATROOM);
		// Check if registry was successful
		if (result == 0) {
			// Find room
/*			ChatRoomServer room = null;
			for (ChatRoomServer chatroom: chatRooms) {
				if (chatroom.getName().equals(room_name)) {
					room = chatroom;
					break;
				}
			}*/
			// Remove room
			chatRooms.remove(room);
			return 0;
		}
		System.err.println("Could not close chat room with name '" + room_name + "'. Error code was " + result);
		return result;

	}

}
