
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ChatRoomServer extends Remote {

	int join(ChatClient client) throws RemoteException;
	
	int talk(String clientName, String message) throws RemoteException;
	
	int leave(String clientName) throws RemoteException;
	
	boolean hasClient() throws RemoteException;
	
	String getName() throws RemoteException;
	
}
