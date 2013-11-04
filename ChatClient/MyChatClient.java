import java.util.List;
import java.util.concurrent.BlockingQueue;


public class MyChatClient implements ChatClient{

	ChatRegistry registry;
	List <ChatRoomServer>  chatrooms;
	String clientName;
	BlockingQueue<String> messageQueue;
	
	MyChatClient(ChatRegistry registry) {
		this.registry = registry;
		this.clientName=null;
		this.chatrooms=null;
	}
	
	@Override
	public int regChatClient(String name, ChatRegistry c) {
		registry.register(name, registry.Type.CHATCLIENT);
		this.clientName=name;
		return 0;
	}
	
	public List<String> findAvailableChatRooms() {
		return registry.getInfo(entityName);
	}

	@Override
	public int joinChatRoom(String name) {
		ChatRoomServer stub = (ChatRoomServer) registry.lookup("name");
		chatrooms.add(stub);
		stub.join(name);
		return 0;
	}

	@Override
	public int leaveChatRoom(String name) {
		for(ChatRoomServer chatRoomSvr : chatrooms) {
            if (chatRoomSvr.getName().equals(name)==true){
            	chatRoomSvr.leave(this.clientName);
            	break;
            }
        }
		return 0;
	}

	@Override
	public int sendMessage(String room, String message) {
		for(ChatRoomServer chatRoomSvr : chatrooms) {
            if (chatRoomSvr.getName().equals(room)==true){
            	chatRoomSvr.talk(this.clientName, message);
            	break;
            }
        }
		return 0;
	}

	@Override
	public void quit() {
		System.out.println("Bye.");
		System.exit(1);
	}

	@Override
	public int receiveMessage(String room, String message) {
		String temp="";
		temp.concat("Msg: ");
		temp.concat(message);
		temp.concat(", [from chat room: ");
		temp.concat(room);
		temp.concat("]");
		messageQueue.add(temp);
		return 0;
	}
}