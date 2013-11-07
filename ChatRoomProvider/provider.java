import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class provider {

	/**
	 * This is where the provider operations are handled. Here, the ChatRegistry stub
	 * is fetched from rmiregistry, and passed to an instantiation of MyChatRoomProvider
	 * ( which represents the chat room provider). Then. the user is prompted to type a command
	 * and the system acts accordingly.
	 * @param args
	 * @throws RemoteException
	 */
	public static void main(String[] args) throws RemoteException {
		
		// Check if the hostname parameter is entered
		if (args.length != 1) {
			System.out.println("Please enter a single argument (i.e. hostname of your registy)!");
			System.exit(0);
		}
		
		String host = args[0];
		ChatRoomProvider x = null;
		
		// Lookup ChatRegistry, fetch the stub and pass it to ChatRoomProvider
		try {
			Registry registry = LocateRegistry.getRegistry(host);
			ChatRegistry stub = (ChatRegistry) registry.lookup("ChatRegistry");
			x = new MyChatRoomProvider(stub);
		} catch (AccessException e) {
			System.err.println("You do not have permission to look up ChatRegistry!");
		} catch (RemoteException e) {
			System.err.println("Remote exception found while attempting to get a remote stub for ChatRegistry");
		} catch (NotBoundException e) {
			System.err.println("An object bound to the name 'ChatRegistry' does not exist!");
		}
		
		// Wait for user input, then perform appropriate function
		while (true) {
			
			System.out.println("\nSupported commands: 'open [room name]', 'close [room name]'");
			String instruction = System.console().readLine();
			String[] instruction_split = instruction.split(" ");
			
			// Check for invalid input and print message if necessary
			if (instruction_split == null) continue;
			if (instruction_split.length != 2) {
				System.err.println("Command must be followed by a name, with no whitespace in room name!");
				continue;
			}
			
			// Check if the user entered "open" or "close", otherwise ignore the entry
			if ("open".equals(instruction_split[0])) {
				
				// Open the chat room, and print out the appropriate message
				int code = x.openChatRoom(instruction_split[1]);
				if (code == 0) {
					System.out.println("Room was opened.");
				} else {
					System.out.println("Room was not opened. Returned error code " + code);
				}
				
			} else if ("close".equals(instruction_split[0])) {
				
				// Close the chat room and print out the appropriate message
				int code = x.closeChatRoom(instruction_split[1]);
				if (code == 0) {
					System.out.println("Room was closed.");
				} else {
					System.out.println("Room was not closed. Returned error code " + code);
				}
				
			}
			
		}
		
	}

}
