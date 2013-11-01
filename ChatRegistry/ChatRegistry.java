import java.rmi.Remote;
import java.rmi.RemoteException;


public interface ChatRegistry extends Remote {

	enum Type {CHATROOM, CHATCLIENT};
	
	int register(String entityName, Type entityType) throws RemoteException;
	
	int deregister(String entityName, Type entityType) throws RemoteException;
	
	String getInfo(String entityName) throws RemoteException;
	
}
