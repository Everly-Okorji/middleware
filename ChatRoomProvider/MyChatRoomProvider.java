import java.util.ArrayList;
import java.util.List;

import server.ChatRoomServer;
import server.MyChatRoomServer;

/**
 * 
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
	public int openChatRoom(String room_name) {
		int result = registry.register(room_name, ChatRegistry.Type.CHATROOM);
		if (result == 0) {
			ChatRoomServer room = new MyChatRoomServer(room_name);
			chatRooms.add(room);
			return 0;
		}
		return result;
	}

	@Override
	public int closeChatRoom(String room_name) {
		
		int result = registry.deregister(room_name, ChatRegistry.Type.CHATROOM);
		// Check if registry was successful
		if (result == 0) {
			// Find room
			ChatRoomServer room = null;
			for (ChatRoomServer chatroom: chatRooms) {
				if (chatroom.getName().equals(room_name)) {
					room = chatroom;
					break;
				}
			}
			// Remove room
			chatRooms.remove(room);
			return 0;
		}
		return result;

	}

}
