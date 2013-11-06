import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class client {

	public static void main(String[] args) throws RemoteException {
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
		
		printMenu();
		
		String userInput;
		String currentChatRoom=null;
	/*	
		messageOutputThread=new Thread(new Runnable () {
			@Override
			public void run() {
				while (true) x.printMessage();
			}
		});
		messageOutputThread.start();*/
		boolean inChatRoomFlag=false;
		while (true) {			
			//x.printMessage();
			System.out.print("Enter: ");
			userInput=System.console().readLine();
			
			if (userInput.isEmpty()) {
				continue;
			}
			
			if (inChatRoomFlag==false){
				if (userInput.length()<5) {
					System.err.println("Invalid command: " + userInput + ". Please enter cmd-* where * is a single claracter.");
					continue;
				}
				else if (userInput.substring(0, 4).equals("cmd-")==false){
					System.err.println("Invalid command: " + userInput + ". Please enter cmd-* where * is a single claracter.");
					continue;
				}
			}
			
			if (userInput.length()>=5){
				if (userInput.substring(0, 4).equals("cmd-") && userInput.length()>=5){
					switch (userInput.charAt(4)){
					case 'm':
						if (userInput.length()==5){ 
							printMenu();
							break;
						}
					case 'f': 
						if (userInput.length()==5){
							List<String> chatRooms=x.findAvailableChatRooms();
							
							System.out.println("Available Chat Rooms: ");
							// Print the chat room names from the list
							for(String chatRoom : chatRooms) {
								System.out.println(chatRoom);
							}
							System.out.println("");
							// Or like this
		//			        for(int i = 0; i < chatRooms.size(); i++) {
		//			            System.out.println(chatRooms.get(i));
		//			        }
							break;
						}
					case 'j': 
						if (userInput.length()==5){
							System.out.print("Please enter chat room you want to join: ");
							String chatRoomNameJoin=System.console().readLine();
							x.joinChatRoom(chatRoomNameJoin);
							break;
						}
					case 'l':
						if (userInput.length()==5){
							System.out.println("Please enter the name of the chat room you want to leave:");
							String chatRoomNameLeave=System.console().readLine();
							if (x.leaveChatRoom(chatRoomNameLeave) != 0) {
								System.out.println("Cannot leave chat room "+chatRoomNameLeave+".");
							}
							else{
								if (chatRoomNameLeave==currentChatRoom) inChatRoomFlag=false;
							}
							break;
						}
					case 'c':
						if (userInput.length()==5){
							
							System.out.println("Please enter the name of the chat room you want to choose to send message:");
							String chatRoomNameChose=System.console().readLine();
							
							
							if (x.checkJoinedRoom(chatRoomNameChose)!=0) {
								System.out.println("Chat room '" + chatRoomNameChose + "' is not a joined room!");
								continue;
							}
							else{
								currentChatRoom=chatRoomNameChose;
								inChatRoomFlag = true;
								System.out.println("Now you can send message to chat room "+chatRoomNameChose+".");
							}
							break;
						}
					case 'q':
						if (userInput.length()==5){
							x.quit();
							break;
						}
					default:
						if (inChatRoomFlag==false) {
							System.err.println("Invalid command: " + userInput + ". Please enter cmd-* where * is a single claracter.");
							continue;
						}
						if (x.sendMessage(currentChatRoom, userInput)==1){
							System.out.println("Cannot send message to chat room "+currentChatRoom+".");
						}
						break;
					}
				}else {	
					if (inChatRoomFlag==false) {
						System.out.println("Please specify a chat room to send message, or enter a valid command.");
						continue;
					}
					if (x.sendMessage(currentChatRoom, userInput)==1){
						System.out.println("Cannot send message to chat room "+currentChatRoom+".");
					}
				}
			}
			else {	
				if (inChatRoomFlag==false) {
					System.out.println("Please specify a chat room to send message, or enter a valid command.");
					continue;
				}
				if (x.sendMessage(currentChatRoom, userInput)==1){
					System.out.println("Cannot send message to chat room "+currentChatRoom+".");
				}
			}
		}
		
	}
	
	private static void printMenu() {
		System.out.println("Your Command Menu:");
		System.out.println("cmd-f: Find currently available chat rooms.");
		System.out.println("cmd-j: Join a chat room.");
		System.out.println("cmd-l: Leave a chat room.");
		System.out.println("cmd-c: Choose a chat room to send message.");
		System.out.println("cmd-q: Choose a chat room to quit.");
		System.out.println("cmd-m: Display command menu.");
	}

}
