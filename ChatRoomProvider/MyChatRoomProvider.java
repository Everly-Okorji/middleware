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
	/*	
		// Check if chat room is present
		if (nameAvailable(room_name)) {
			System.err.println("No chat room exists with name '" + room_name + "'!");
			return 1;
		}
		
		// Fetch chat room object
		ChatRoomServer chatRoom = null;
		for (ChatRoomServer room: chatRoomServers) {
			if (room.getName().equals(room_name)) {
				chatRoom = room;
			}
		}
		
		// Deregister chat room from registry
		if (registry.deregister(room_name, ChatRegistry.Type.CHATROOM) == 0) {
			chatRoomServers.remove(chatRoom);
			roomNames.remove(room_name);
			return 0;
		}
		
		System.err.println("Registry could not remove chat room with name '" + room_name + "'!");
		return 2;
		*/
	}

}
