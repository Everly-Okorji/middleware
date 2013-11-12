import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * This is the main function of the ChatClinet, supporting a basic user interface.
 * @author Zirui Wang
 *
 */

public class client {
	
	public static ChatClient x;
	public static void main(String[] args) throws RemoteException {
		
		if (args.length != 1) {
			System.out.println("Please enter a single argument (i.e. hostname of your registy)!");
			System.exit(0);
		}
		String host = args[0];
		
		ChatRegistry stub = null;
		
		//Find the ChatRegistry by looking up the registry and acquire the stub of ChatRegistry
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
		//A flag to mark if the username the client inputed is valid. 0: valid, other: invalid
		int result = 1;
		
		// Fetch valid username
		do {
			
			System.out.print("Please enter a username: ");
			name = System.console().readLine();

			//Check if the name is a empty or null. i.e. nothing did the client typed in
			if (name == null) {
				continue;
			}
			
			if (name.isEmpty()) {
				continue;
			}
			
			//try to register the client with the username, get returen value 0 if successful.
			x = new MyChatClient(stub, name);
			result = x.regChatClient();
			if (result != 0) {
				System.err
						.println("Username already exists or cannot be used. Please use a different name!");
			}
			
		} while (result != 0);
		
		System.out.println("Welcome, " + name + "!");
		
		// Launch the user interface
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                 //Turn off metal's use of bold fonts
		UIManager.put("swing.boldMetal", Boolean.FALSE);
		UserInterface.createAndShowGUI();
            }
        });
		
		//Print the command menu for the client
		printMenu();
		
		String userInput;
		String currentChatRoom=null;
	
		boolean inChatRoomFlag=false;
		UserInterface.inChatRoom = false;
		
		while (true) {			
			//The loop will keep read inputs from the client and judge if the recent input is a command, or a message
			//The command will be executed if possible, the messages will be sent if the client is in some chat rooms 
			//and has chosen one chat room to chat 
			
			//Get the input from the console
			System.out.print("Enter: ");
			userInput=System.console().readLine();
			
			if (userInput.isEmpty()) {
				continue;
			}
			
			//if the client is not in any chat room, he/she will only be allowed to type in a command starting with "cmd-"
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
			
			//The following part will do two things:
			//1. judge if the input is a command or a message
			//2. execute the command or send the message, if there is no restriction on this action 
			if (userInput.length()>=5){
				if (userInput.substring(0, 4).equals("cmd-") && userInput.length()>=5){
					//The input is a potential cmd-x
					//it can be cmd-x* e.g. cmd-xhaha, which should not be considered as a cmd, but a message.
					switch (userInput.charAt(4)){
					case 'm':
						if (userInput.length()==5){ 
							//cmd-m
							printMenu();
							break;
						}
					case 'f': 
						if (userInput.length()==5){
							//cmd-f
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
							//cmd-j
							x.printJoinedRooms();
							
							System.out.print("Please enter chat room you want to join: ");
							String chatRoomNameJoin=System.console().readLine();
							x.joinChatRoom(chatRoomNameJoin);
							System.out.println("Use cmd-c to choose a chat room where you want to chat.");
							break;
						}
					case 'l':
						if (userInput.length()==5){
							//cmd-l
							x.printJoinedRooms();
							
							//Ask for which chat room to leave
							System.out.println("Please enter the name of the chat room you want to leave:");
							String chatRoomNameLeave=System.console().readLine();
							
							//Leave the chat room
							if (x.leaveChatRoom(chatRoomNameLeave) != 0) {
								System.out.println("Cannot leave chat room "+chatRoomNameLeave+".");
							}
							else{
								//update the inChatRoomFlage to indicate if the client has now chose a chat room to chat
								if (chatRoomNameLeave==currentChatRoom) {
									inChatRoomFlag=false;
									UserInterface.inChatRoom = false;
								}
							}
							break;
						}
					case 'c':
						if (userInput.length()==5){
							//cmd-c
							x.printJoinedRooms();
							
							System.out.println("Please enter the name of the chat room you want to choose to send message:");
							String chatRoomNameChose=System.console().readLine();
							
							//Check if the client has joined the chosen chat room
							if (x.checkJoinedRoom(chatRoomNameChose)!=0) {
								System.out.println("Chat room '" + chatRoomNameChose + "' is not a joined room!");
								continue;
							}
							else{
								//update the inChatRoomFlage to indicate if the client has now chose a chat room to chat
								currentChatRoom=chatRoomNameChose;
								UserInterface.currentRoom = chatRoomNameChose;
								inChatRoomFlag = true;
								UserInterface.inChatRoom = true;
								System.out.println("Now you can send message to chat room "+chatRoomNameChose+".");
							}
							break;
						}
					case 'q':
						if (userInput.length()==5){
							//cmd-q
							x.quit();
							break;
						}
					default:
						//The input looks like a command but is actually not, and should be seen as a message
						if (inChatRoomFlag==false) {
							//Haven't chosen any chat room to chat, a command is expected
							System.err.println("Invalid command: " + userInput + ". Please enter cmd-* where * is a single claracter.");
							continue;
						}
						
						//Send the message
						if (x.sendMessage(currentChatRoom, userInput)==1){
							System.out.println("Cannot send message to chat room "+currentChatRoom+".");
						}
						break;
					}
				}else {	
					//The input should be considered as a message, and the message length is >=5
					if (inChatRoomFlag==false) {
						//Haven't chosen any chat room to chat
						System.out.println("Please specify a chat room to send message, or enter a valid command.");
						continue;
					}
					
					//Send the message
					if (x.sendMessage(currentChatRoom, userInput)==1){
						System.out.println("Cannot send message to chat room "+currentChatRoom+".");
					}
				}
			}
			else {	
				//The input should be considered as a message, and the message length is <5
				if (inChatRoomFlag==false) {
					//Haven't chosen any chat room to chat
					System.out.println("Please specify a chat room to send message, or enter a valid command.");
					continue;
				}
				
				//Send the message
				if (x.sendMessage(currentChatRoom, userInput)==1){
					System.out.println("Cannot send message to chat room "+currentChatRoom+".");
				}
			}
		}
		
	}
	
	//Print out the command menu
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
