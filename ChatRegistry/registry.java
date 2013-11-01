
import java.rmi.AccessException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class registry {

	public static void main(String[] args) {
		
		try{
		    MyChatRegistry reg = new MyChatRegistry();
		    ChatRegistry stub = (ChatRegistry) UnicastRemoteObject.exportObject(reg, 0);
		    
		    Registry registry = LocateRegistry.getRegistry();
		    registry.rebind("ChatRegistry", stub);
		} catch (AccessException e) {
			System.err.println("You do not have permission to bind ChatRegistry to rmi registry!");
		} catch (RemoteException e) {
			System.err.println("Remote exception found while binding ChatRegistry to the rmi registry!");
			e.printStackTrace();
		}
		
	}

}
