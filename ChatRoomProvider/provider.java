import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class provider {

	public static void main(String[] args) {
		ChatRoomProvider x = null;
		String host = args[0];
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
		
		
		while (true) {
			
			System.out.println("\n Supported commands: 'open [room name]', 'close [room name]'");
			System.out.print("Enter command: ");
			
			String instruction = System.console().readLine();
			String[] instruction_split = instruction.split(" ");
			
			if (instruction_split == null) continue;
			if (instruction_split.length != 2) continue;
			
			if ("open".equals(instruction_split[0])) {
				int code = x.openChatRoom(instruction_split[1]);
				if (code == 0) {
					System.out.println("Room was opened.");
				} else {
					System.out.println("Room was not opened. Returned error code " + code);
				}
			} else if ("close".equals(instruction_split[0])) {
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
