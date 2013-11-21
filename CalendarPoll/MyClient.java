import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public class MyClient implements Client {

	String name;
	
	List<String> clientList;
	
	List<Poll> polls;
	
	Map<String, Queue> map;
	
	List<String> peopleNames;//need to get input from file!!!
	
	List<Poll> pollMsg;
	
	MessageHandler message;
	
	static Context ictx=null;
	QueueConnectionFactory qcf;
	static Map<String, QueueConnection> openConnections;
	
	MyClient (String name, List<String> clientList) { 
		this.name=name;
		this.clientList=clientList;
		this.polls=new ArrayList<Poll>();
		this.pollMsg=new ArrayList<Poll>();
		this.openConnections = new HashMap<String, QueueConnection>();
		
		// Create a queue for each registered name
		map = new HashMap<String, Queue>();
		
		try {
			ictx = new InitialContext();
			qcf = (QueueConnectionFactory) ictx.lookup("qcf");
		    
			for (String people: peopleNames){
				 Queue queue = (Queue) ictx.lookup(people);	
				 map.put(people, queue);
			}
			ictx.close();
		} catch (NamingException e) {
			System.err.println("Naming exception found at MyClient constructor");
			System.exit(1);
		} 
	    
	}

	@Override
	public void createPoll(String title, String message,
			List<String> possibleTimes) {
		Poll p=new MyPoll(title, message, name, MyPoll.Status.INITIALIZED, possibleTimes);
		polls.add(p);
	}
	
	@Override
	public void sendPoll(String title, Set<String> members) {
		
		boolean pollfound=false;
		Poll poll=null;
		
		for (Poll pIterator: polls){
			if (pIterator.getTitle().equals(title)) {
				poll=pIterator;
				pollfound=true;
				break;
			}
		}
		
		if (pollfound==false){
			System.err.println("There is no such poll titled:"+title+".");
			return;
		}
		
		// Send poll to everyone in the poll list
		for (String person : peopleNames) {
			if (members.contains(person)) { // Person will be receiving message

				try {
					// Get the QueueConnection object, or create one if it does not exist
					if (!openConnections.containsKey(person)) {
						QueueConnection conn = qcf.createQueueConnection();
						openConnections.put(person, conn);
					}
					QueueConnection cnx = openConnections.get(person);
					QueueSession session = cnx.createQueueSession(false,
							Session.AUTO_ACKNOWLEDGE);

					TemporaryQueue tempQueue = session.createTemporaryQueue();
					QueueSender sender = session.createSender(map.get(person));
					cnx.start();

					ObjectMessage msg = session.createObjectMessage();
					msg.setObject(poll);
					msg.setJMSReplyTo(tempQueue);
					
					sender.send(msg);
				} catch (JMSException e) {
					System.err.println("Message not sent! JMS Exception found.");
					return;
				}
				
				pollMsg.add(poll); // TODO Do we need this? NO?
				
				poll.setOpen();

				System.out.println("Poll sent!");

			}
		}

	}
	
	@Override
	public void UpdatePollInfo(Response response) {
		
		// Poll response from message handler. Need to handle it
		boolean pollfound=false;
		Poll poll=null;
		
		for (Poll pIterator: polls){
			if (pIterator.getTitle().equals(response.getPollName())) {
				poll=pIterator;
				pollfound=true;
				break;
			}
		}
		
		if (pollfound==false){
			System.err.println("There is no such poll titled:"+response.getPollName()+".");
		}
		else{
			
			// TODO Also check if the poll status is Status.OPEN before accepting responses
		}
		
	}
	
	@Override
	public void closePoll(String poll_name) {
		boolean pollfound=false;
		Poll poll=null;
		
		for (Poll pIterator: polls){
			if (pIterator.getTitle().equals(poll_name)) {
				poll=pIterator;
				pollfound=true;
				break;
			}
		}
		if (pollfound==false){
			System.err.println("There is no such poll titled:"+poll_name+".");
		}
		else{
			poll.setFinalized();
		}
		
	}
	
	@Override
	public void receivePoll(Poll poll) {
		//message.receiveMessage(poll);
	}
	
	@Override
	public Response setAvailability(List<String> availability) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void sendResponse(Response response) {
		// TODO Auto-generated method stub
		
	}

}
