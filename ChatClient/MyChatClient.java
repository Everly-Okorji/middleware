import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;


public class MyChatClient implements ChatClient{

	ChatRegistry registry;
	List <ChatRoomServer>  joinedRooms;
	String clientName;
	BlockingQueue<String> messageQueue;
	
	MyChatClient(ChatRegistry registry, String name) {
		this.registry = registry;
		this.clientName=name;
		this.joinedRooms=new ArrayList<ChatRoomServer>();
	}
	
	@Override
	public int regChatClient() throws RemoteException {
		if (registry.register(this, ChatRegistry.Type.CHATCLIENT)!=0){
			System.err.println("Try to register client "+clientName+" but failed!");
			return 1;
		}
		return 0;
	}
	
	@Override
	public List<String> findAvailableChatRooms() throws RemoteException {
		return registry.getChatRoomsList();
	}

	@Override
	public int joinChatRoom(String name) throws RemoteException {
		ChatRoomServer chatroom=null;
		chatroom=(ChatRoomServer) registry.getInfo(name,ChatRegistry.Type.CHATROOM);
		joinedRooms.add(chatroom);
		return 0;
	}

	@Override
	public int leaveChatRoom(String name) throws RemoteException {
		for(ChatRoomServer room:joinedRooms) {
            if (room.getName().equals(name)==true){
            	if (room.leave(clientName)==0){
            		joinedRooms.remove(room);
            	}
            	else {
            		System.err.println("Try to leave chat room "+room.getName()+" but failed!");
            		return 1;
            	}
            	break;
            }
        }
		return 0;
	}

	@Override
	public int sendMessage(String room, String message) throws RemoteException {
		for(ChatRoomServer r: joinedRooms) {
            if (r.getName().equals(room)==true){
            	if (r.talk(clientName, message)!=0){
            		System.err.println("Try to send message to chat room "+room+" but failed!");
            		return 1;
            	}
            	break;
            }
        }
		return 0;
	}

	@Override
	public int quit() throws RemoteException {
		for(ChatRoomServer room:joinedRooms) {
          	if (room.leave(clientName)==0){
        		joinedRooms.remove(room);
        	}
        	else {
        		System.err.println("In quit(): Try to leave chat room "+room.getName()+" but failed!");
        		return 1;
        	}
        }
		if (registry.deregister(clientName, ChatRegistry.Type.CHATCLIENT)!=0){
			System.err.println("In quit(): Try to deregistry client "+clientName+" but failed!");
    		return 2;
		}
		System.out.println("Bye.");
		return 0;
	}

	@Override
	public int receiveMessage(String room, String message) {
		String temp="";
		temp.concat("[");
		temp.concat(room);
		temp.concat("]:");
		temp.concat(message);
		messageQueue.add(temp);
		return 0;
	}

	@Override
	public String getName() throws RemoteException {
		return clientName;
	}
	
	public void printMessage(){
		while (messageQueue.isEmpty()==false){
			System.out.println(messageQueue.remove());
		}
	}
}