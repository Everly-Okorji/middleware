import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is the implementation of ChatClient.
 * @author Zirui Wang
 *
 */
public class MyChatClient extends UnicastRemoteObject implements ChatClient{

	ChatRegistry registry;
	List <ChatRoomServer>  joinedRooms;
	String clientName;
	
	Thread messageOutputThread;
	
	//The constructor
	MyChatClient(ChatRegistry registry, String name) throws RemoteException {
		this.registry = registry;
		this.clientName=name;
		this.joinedRooms=new ArrayList<ChatRoomServer>();
		
	}
	
	//Register the client with the chatRegistry
	@Override
	public int regChatClient() throws RemoteException {
		if (registry.register(this, ChatRegistry.Type.CHATCLIENT)!=0){
			System.err.println("Tried to register client '"+clientName+"' but failed!");
			return 1;
		}
		return 0;
	}
	
	//Get a list of current open(available) chat rooms registered on the ChatRegistry
	@Override
	public List<String> findAvailableChatRooms() throws RemoteException {
		return registry.getChatRoomsList();
	}

	//Try to join a chat room
	@Override
	public int joinChatRoom(String name) throws RemoteException {
		ChatRoomServer chatroom=null;
		chatroom=(ChatRoomServer) registry.getInfo(name,ChatRegistry.Type.CHATROOM);
		
		if (chatroom == null) {
			//The chat room does not exist
			System.err.println("Chat room '" + name + "' does not exist!");
			return 1;
		}
		chatroom.join(this);
		joinedRooms.add(chatroom);
		return 0;
	}
	
	//Try to leave a chat room
	@Override
	public int leaveChatRoom(String name) throws RemoteException {
		
		// Find the chat room which the user wants to leave
		for(ChatRoomServer room:joinedRooms) {
            if (room.getName().equals(name)==true){
            	if (room.leave(clientName)==0){
            		joinedRooms.remove(room);
            		return 0;
            	}
            	else {
            		System.err.println("Tried to leave chat room "+name+" but failed!");
            		return 1;
            	}
            }
        }
		System.err.println("Chat room " + name + " is not one of your joined rooms!");
		return 2;
	}

	//Try to send the message to the specified chat room
	@Override
	public int sendMessage(String room, String message) throws RemoteException {
		for(ChatRoomServer r: joinedRooms) {
            if (r.getName().equals(room)){
            	if (r.talk(clientName, message)!=0){
            		System.err.println("Tried to send message to chat room "+room+" but failed!");
            		return 1;
            	} else {
            		return 0;
            	}
            }
        }
		System.err.println("Chat room " + room + " is not one of your joined rooms!");
		return 2;
	}

	
	@Override
	public void quit() throws RemoteException {
		//Quit, this requires 3 thins to do in sequence:
		//1.leave all the chat rooms the client joined
		//2.deregistered the chat client from the ChatRegistry
		//3.exit the system
		
		// Leave all joined chat rooms
		Iterator<ChatRoomServer> it = joinedRooms.iterator();
		while (it.hasNext()) {
			
			ChatRoomServer room = it.next();
			if (room.leave(clientName) == 0) {
				it.remove();
			} else {
				System.err.println("In quit(): Tried to leave chat room "
						+ room.getName() + " but failed!");
			}
		}
		
		// Deregister client
		if (registry.deregister(clientName, ChatRegistry.Type.CHATCLIENT) != 0) {
			System.err.println("In quit(): Try to deregistry client "
					+ clientName + " but failed!");
		}
		
		System.out.println("Bye.");
		
		//Exit the system
		System.exit(0);
	}

	//Receive a message and add it to the messageQueue
	@Override
	public int receiveMessage(String room, String message) {
		String temp="";
		temp=temp.concat("[");
		temp=temp.concat(room);
		temp=temp.concat("]:");
		temp=temp.concat(message);
		UserInterface.queueMessage(temp);
		//System.out.println(temp);
		return 0;
	}

	//Get the client's name
	@Override
	public String getName() throws RemoteException {
		return clientName;
	}
	
	//Check if the client is in the specific chat room
	@Override
	public int checkJoinedRoom(String roomname) throws RemoteException{
		for(ChatRoomServer chatRoom : joinedRooms) {
            if (chatRoom.getName().equals(roomname)){
            	System.out.println("Found joined chat room "+roomname+".");
            	return 0;
            }
        }
		System.out.println("Can not find joined chat room "+roomname+".");
		return 1;
	}
	
	//Print out the list of chat rooms the client has joined
	@Override
	public void printJoinedRooms() throws RemoteException {
		System.out.println("The list of your current joined chat rooms:");
		for(ChatRoomServer chatRoom : joinedRooms) {
            System.out.println("chat room: "+chatRoom.getName());
        }		
	}
}