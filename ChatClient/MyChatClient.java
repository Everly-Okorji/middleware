import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class MyChatClient extends UnicastRemoteObject implements ChatClient{

	ChatRegistry registry;
	List <ChatRoomServer>  joinedRooms;
	String clientName;
	BlockingQueue<String> messageQueue;
	
	Thread messageOutputThread;
	
	MyChatClient(ChatRegistry registry, String name) throws RemoteException {
		this.registry = registry;
		this.clientName=name;
		this.joinedRooms=new ArrayList<ChatRoomServer>();
		this.messageQueue = new LinkedBlockingQueue<String>();
		
		messageOutputThread=new Thread(new Runnable () {
			@Override
			public void run() {
				while (true) {
					try {
						printMessage();
					} catch (RemoteException e) {

					}
				}
			}
		});
		messageOutputThread.start();
		
	}
	
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
	
	@Override
	public int regChatClient() throws RemoteException {
		if (registry.register(this, ChatRegistry.Type.CHATCLIENT)!=0){
			System.err.println("Tried to register client '"+clientName+"' but failed!");
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
		
		if (chatroom == null) {
			System.err.println("Chat room '" + name + "' does not exist!");
			return 1;
		}
		chatroom.join(this);
		joinedRooms.add(chatroom);
		return 0;
	}

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
		/*
		for (ChatRoomServer room : joinedRooms) {
			if (room.leave(clientName) == 0) {
				joinedRooms.remove(room);
			} else {
				System.err.println("In quit(): Tried to leave chat room "
						+ room.getName() + " but failed!");
			}
		}*/
		
		// Deregister client
		if (registry.deregister(clientName, ChatRegistry.Type.CHATCLIENT) != 0) {
			System.err.println("In quit(): Try to deregistry client "
					+ clientName + " but failed!");
		}
		
		System.out.println("Bye.");
		System.exit(0);
	}

	@Override
	public int receiveMessage(String room, String message) {
		String temp="";
		temp=temp.concat("[");
		temp=temp.concat(room);
		temp=temp.concat("]:");
		temp=temp.concat(message);
		messageQueue.add(temp);
		//System.out.println(temp);
		return 0;
	}

	@Override
	public String getName() throws RemoteException {
		return clientName;
	}
	
	public void printMessage() throws RemoteException {
		try {
			System.out.println(messageQueue.take());
		} catch (InterruptedException e) {
			System.err.println("InterruptedException found while printing message.");
		}
	}
}