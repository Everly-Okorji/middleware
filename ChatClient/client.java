import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;

public class client {

	public static void main(String[] args) throws RemoteException {
		Thread messageOutputThread;
		String host = args[0];
		
		ChatClient x = null;
		ChatRegistry stub = null;
		
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			stub = (ChatRegistry) registry.lookup("ChatRegistry");
		} catch (AccessException e) {
			System.err
					.println("You do not have permission to look up ChatRegistry!");
			System.exit(1);
		} catch (RemoteException e) {
			System.err
					.println("Remote exception found while attempting to get a remote stub for ChatRegistry");
			System.exit(1);
		} catch (NotBoundException e) {
			System.err
					.println("An object bound to the name 'ChatRegistry' does not exist!");
			System.exit(1);
		}
		
		String name;
		int result = 1;
		
		// Fetch valid username
		do {
			
			System.out.print("Please enter a username: ");
			name = System.console().readLine();

			x = new MyChatClient(stub, name);
			result = x.regChatClient();
			if (result != 0) {
				System.err
						.println("Username already exists or cannot be used. Please use a different name!");
			}
			
		} while (result != 0);
		
		System.out.println("Welcome, " + name + "!");
		
		System.out.println("To Call the Command Menu by simply type \"cmd-m\".");
		String userInput;
		List<String> chatRooms=null;
		List<String> joinedChatRooms=new ArrayList<String>();
		boolean noSuchChatRoom=true;
		String currentChatRoom=null;
	
		char c = System.console().readLine().charAt(0);
		switch (c) {	
		
		case 'r':
			x.regChatClient();
			break;
		case 'd':
			x.quit();
			break;
			
			default:
				
				break;
				
		}
		
/*		messageOutputThread=new Thread(new Runnable () {
			@Override
			public void run() {
				while (true) x.printMessage();
			}
		});
		messageOutputThread.start();*/
		
		/*while (true) {			
			x.printMessage();
			userInput=System.console().readLine();
			if (userInput.substring(0, 3).equals("cmd-")){
				switch (userInput.charAt(0)){
				case 'm':
					System.out.println("Your Command Menu:");
					System.out.println("cmd-f: Find currently available chat rooms.");
					System.out.println("cmd-j: Join a chat room.");
					System.out.println("cmd-l: Leave a chat room.");
					System.out.println("cmd-c: Choose a chat room to send message.");
					System.out.println("cmd-q: Choose a chat room to quit.");
					System.out.println("To Call the Command Menu by simply type \"cmd-m\".");
					break;
				case 'f': 
					chatRooms=x.findAvailableChatRooms();
					// Print the chat room names from the list
			        for(String chatRoom : chatRooms) {
			            System.out.println(chatRoom);
			        }

			        // Or like this
//			        for(int i = 0; i < chatRooms.size(); i++) {
//			            System.out.println(chatRooms.get(i));
//			        }
					break;
				case 'j': 
					System.out.println("Please enter the name of the chat room you want to join:");
					String chatRoomNameJoin=System.console().readLine();
					noSuchChatRoom=true;
					for(String chatRoom : chatRooms) {
			            if (chatRoom.equals(chatRoomNameJoin)==true){
			            	System.out.println("Found chat room "+chatRoomNameJoin+".");
			            	noSuchChatRoom=false;
			            	break;
			            }
			        }
					if (noSuchChatRoom==true) System.out.println("Cannot find chat room "+chatRoomNameJoin+".");
					else{
						if (x.joinChatRoom(chatRoomNameJoin)==0){
							System.out.println("Joined chat room "+chatRoomNameJoin+".");
							joinedChatRooms.add(chatRoomNameJoin);
						}
						else {
							System.out.println("Cannot join chat room "+chatRoomNameJoin+".");
							System.exit(1);
						}
					}
					break;
				case 'l':
					System.out.println("Please enter the name of the chat room you want to leave:");
					String chatRoomNameLeave=System.console().readLine();
					noSuchChatRoom=true;
					for(String chatRoom : joinedChatRooms) {
			            if (chatRoom.equals(chatRoomNameLeave)==true){
			            	System.out.println("Found joined chat room "+chatRoomNameLeave+".");
			            	noSuchChatRoom=false;
			            	break;
			            }
			        }
					if (noSuchChatRoom==true) System.out.println("Cannot find joined chat room "+chatRoomNameLeave+".");
					else{
						if (x.joinChatRoom(chatRoomNameLeave)==0){
							System.out.println("Left chat room "+chatRoomNameLeave+".");
							joinedChatRooms.remove(chatRoomNameLeave);
						}
						else {
							System.out.println("Cannot leave chat room "+chatRoomNameLeave+".");
							System.exit(1);
						}
					}
					break;
				case 'c':
					System.out.println("Please enter the name of the chat room you want to choose to send message:");
					String chatRoomNameChose=System.console().readLine();
					noSuchChatRoom=true;
					for(String chatRoom : joinedChatRooms) {
			            if (chatRoom.equals(chatRoomNameChose)==true){
			            	System.out.println("Found joined chat room "+chatRoomNameChose+".");
			            	noSuchChatRoom=false;
			            	break;
			            }
			        }
					if (noSuchChatRoom==true) System.out.println("Cannot find joined chat room "+chatRoomNameChose+".");
					else{
						currentChatRoom=chatRoomNameChose;
						System.out.println("Now you can send message to chat room "+chatRoomNameChose+".");
					
					}
				case 'q':
					if (x.quit()==1) System.exit(1);	
					System.exit(0);
					break;
				default: break;
				}
			}
			else{				
				if (x.sendMessage(currentChatRoom, userInput)==1){
					System.out.println("Cannot send message to chat room "+currentChatRoom+".");
					System.exit(1);
				}
			}
		}*/
		
	}

}
