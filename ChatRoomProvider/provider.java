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
			System.out.print("Enter a key: ");
			switch(System.console().readLine().charAt(0)) {
			case 'o':
				System.out.print("Enter chat room name to open: ");
				System.out.println("Result: " + x.openChatRoom(System.console().readLine()));
				break;
			case 'c':
				System.out.print("Enter chat room name to close: ");
				System.out.println("Result: " + x.closeChatRoom(System.console().readLine()));
				break;
			}
		}
		
	}

}
