import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface ChatRegistry extends Remote {

	enum Type {CHATROOM, CHATCLIENT};
	
	int register(Object entity, Type entityType) throws RemoteException;
	
	int deregister(String entityName, Type entityType) throws RemoteException;
	
	Object getInfo(String entityName, Type entityType) throws RemoteException;
	
	List<String> getClientsList() throws RemoteException;
	
	List<String> getChatRoomsList() throws RemoteException;
	
}
