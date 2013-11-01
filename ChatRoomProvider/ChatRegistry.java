import java.rmi.Remote;


public interface ChatRegistry extends Remote {

	enum Type {CHATROOM, CHATCLIENT};
	
	int register(String entityName, Type entityType);
	
	int deregister(String entityName, Type entityType);
	
	String getInfo(String entityName);
	
}
