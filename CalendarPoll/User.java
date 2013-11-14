import java.util.Arrays;
import java.util.List;


public class User {

	static final String[] clients = {"alice", "tom", "everly", "zirui"};
	
	static String user;
	static Client client;
	
	public static void main(String[] args) {
		
		// Fetch the clients list
		List<String> clientsList = Arrays.asList(clients);
		
		// Fetch username
		user = null;
		while (user == null) {
			System.out.print("Please enter a name: ");
			user = System.console().readLine();
			if (!clientsList.contains(user.toLowerCase())) {
				System.err.println(user + " is not a registered name!");
				user = null;
			}
		}
		
		// Create client object
		client = new MyClient(user, clientsList);
		
		new UserInterface();
		new MyMessageHandler();
	}

}
