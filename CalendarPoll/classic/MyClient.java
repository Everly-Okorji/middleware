package classic;

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
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import classic.Response.RespType;


public class MyClient implements Client {

	String name;
	
	List<String> clientList;
	
	List<Poll> polls;
	
	Map<String, Queue> map;
	
	MessageHandler message;
	
	static Context ictx=null;
	QueueConnectionFactory qcf;
	static Map<String, QueueConnection> openConnections;
	
	static List<Poll> receivedPolls=new ArrayList<Poll>();
	
	MyClient (String name, List<String> clientList) { 
		this.name=name;
		this.clientList=clientList;
		this.polls=new ArrayList<Poll>();
		this.openConnections = new HashMap<String, QueueConnection>();
		
		// Create a queue for each registered name
		map = new HashMap<String, Queue>();
		
		try {
			ictx = new InitialContext();
			qcf = (QueueConnectionFactory) ictx.lookup("qcf");
		    
			for (String people: User.clients){
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
		
		if (title==null){
			System.err.println("Cannot create this poll. The poll's title is 'null'.");
			return;
		}
		
		for (Poll pIterator:polls){
			if (pIterator.getTitle().equals(title)){
				System.err.println("Cannot create this poll. The poll title '"+title+"' is already used!");
				return;
			}
		}
		
		if (message==null){
			System.err.println("Cannot create this poll. The poll's message is 'null'.");
			return;
		}
		
		if (possibleTimes==null){
			System.err.println("Cannot create this poll. The poll's possibleTimes is 'null'.");
			return;
		}
		
		Poll p=new MyPoll(title, message, name, MyPoll.Status.INITIALIZED, possibleTimes);
		polls.add(p);
		
		UserInterface.addMessage("Poll '"+title+"' created.");
	}
	
	@Override
	public void sendPoll(String title, Set<String> members) {
		
		boolean pollfound=false;
		Poll poll=null;
		
		if (members==null){
			System.err.println("Cannot send this poll. The poll's member is 'null'.");
			return;
		}
		
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
		
		//Add members to the poll
		poll.setMembers(members);
		
		
		// Send poll to everyone in the poll list
		for (String person : User.clients) {
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
					
					
					User.mHandler.addNewListener(poll.getTitle(), tempQueue);
					sender.send(msg);
				} catch (JMSException e) {
					System.err.println("Message not sent! JMS Exception found.");
					return;
				}
				
				
				poll.setOpen();

				UserInterface.addMessage("Poll '"+ title+"' has been sent to person '"+person+"'.");

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
			return;
		}
		
		//Check if the poll status is Poll.Status.OPEN before accepting responses
		if (poll.getStatus()!=Poll.Status.OPEN){
			System.err.println("The poll '"+response.poll_name+"' is not open.");
			return;
		}
		
		poll.addResponse(response);
		
		UserInterface.addMessage("Response of poll '"+response.poll_name+"' received from person '"+response.replier+"'.");

	}
	
	@Override
	public void closePoll(String poll_name, String finalizedMeetingTime) {
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
		
		String text=poll_name+finalizedMeetingTime;
		
		
		Set<String> members=poll.getMembers();
		for (String member:members){
			try {
				//TODO new session created, will there be any conflicts with the earlier created session to send poll?
				QueueConnection cnx = openConnections.get(member);
				QueueSession session = cnx.createQueueSession(false,
						Session.AUTO_ACKNOWLEDGE);
				TextMessage msg = session.createTextMessage();
				msg.setText(text);
				QueueSender sender;
				sender = session.createSender(map.get(member));
				sender.send(msg);
				UserInterface.addMessage("Final meeting time of poll'"+poll_name+"' has been sent to person '"+member+"'.");
			} catch (JMSException e) {
				System.err.println("Final meeting time not sent! JMS Exception found.");
				return;
			}
			
		}
		
		UserInterface.addMessage("Poll '"+poll_name+"' has been closed");
		
	}
	
	@Override
	public void receivePoll(Poll poll) {
		
		receivedPolls.add(poll);
		UserInterface.addMessage("New poll '"+poll.getTitle()+"' comes.");
		
	}
	
	@Override
	public Response createResponse(String poll_name, List<String> possible_times, List<RespType> responses) {
		// TODO Check for null poll name and possible_times and responses
		if (poll_name==null){
			System.err.println();
		}
		
		// TODO Check if poll is in received polls. If so, get possible times from there. if not, error.
		
		Response myResponse= new Response(poll_name, name, possible_times, responses);
		return myResponse;
	}
	
	@Override
	public void sendResponse(String poll_name, Response response) {
		// TODO Auto-generated method stub
		User.mHandler.sendResponse(poll_name, response);
	}
	
}
