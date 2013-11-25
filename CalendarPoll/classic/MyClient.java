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
	
	Map<String, QueueConnection> openConnections;	// Maps a poll to its connection object
	Map<String, QueueSession> openSessions;
	
	static List<Poll> receivedPolls=new ArrayList<Poll>();
	
	MyClient (String name, List<String> clientList) { 
		this.name=name;
		this.clientList=clientList;
		this.polls=new ArrayList<Poll>();
		this.openConnections = new HashMap<String, QueueConnection>();
		this.openSessions = new HashMap<String, QueueSession>();
		
		// Create a queue for each registered name
		map = new HashMap<String, Queue>();
		
		try {
			ictx = new InitialContext();
			qcf = (QueueConnectionFactory) ictx.lookup("qcf");
		    
			for (String people: User.other_clients){
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
		
		UserInterface.addMessage("Poll Created: '"+title+"'");
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
		
		poll.setOpen();
		QueueConnection cnx;
		QueueSession session = null;
		TemporaryQueue tempQueue;
		ObjectMessage msg;
		
		// Create the message
		try {
			
			cnx =  qcf.createQueueConnection();
			session = cnx.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			tempQueue = session.createTemporaryQueue();
			cnx.start();

			msg = session.createObjectMessage();
			msg.setObject(poll);
			msg.setJMSReplyTo(tempQueue);
			
		} catch (JMSException e) {
			System.err.println("JMS Exception found while preparing message for '" + poll.getTitle() + "'.");
			return;
		}
		
		// Start listening for responses
		User.mHandler.addNewListener(poll.getTitle(), tempQueue);
		
		// Save the connection object
		openConnections.put(poll.getTitle(), cnx);
		openSessions.put(poll.getTitle(), session);
		
		// Send poll to everyone in the poll list
		for (String person : User.other_clients) {
			if (members.contains(person)) { // Person will be receiving message
				QueueSender sender;
				try {
					sender = session.createSender(map.get(person));
					sender.send(msg);
				} catch (JMSException e) {
					System.err.println("JMS Exception found while trying to send poll '" + poll.getTitle() + "' to " + person + ".");
					continue;
				}
				UserInterface.addMessage("Poll Sent: '"+ title+"' has been sent to '"+person+"'.");
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
		
			if (poll.getStatus() == Poll.Status.INITIALIZED) {
				System.err.println("The poll '"+response.poll_name+"' is still in INITIALIZED status.");
				return;
			}
			if (poll.getStatus() == Poll.Status.FINALIZED) {
				System.err.println("The poll '"+response.poll_name+"' has already been finalized.");
				return;
			}	
		
		
		poll.addResponse(response);
		
		UserInterface.addMessage("Response Received: Response for '"+response.poll_name+"' received from '"+response.replier+"'.");

	}
	
	@Override
	public void closePoll(String poll_name, String finalizedMeetingTime) {
		boolean pollfound=false;
		Poll poll=null;
		
		// Fetch the poll
		for (Poll pIterator: polls){
			if (pIterator.getTitle().equals(poll_name)) {
				poll=pIterator;
				pollfound=true;
				break;
			}
		}
		if (pollfound==false){
			System.err.println("There is no such poll titled:"+poll_name+".");
			return;
		}
		
		poll.setFinalized();
		
		String text= User.user + " selected " + finalizedMeetingTime + " as the finalized meeting time for Poll '" + poll_name + "'.";
		Set<String> members=poll.getMembers();
		
		QueueConnection cnx = openConnections.get(poll_name);
		QueueSession session;
		TextMessage msg;
		QueueSender sender;
		
		try {
			session = cnx.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			msg = session.createTextMessage();
			msg.setText(text);
		} catch (JMSException e1) {
			System.out.println("JMS Exception found while preparing finalized time message for '" + poll_name + "'");
			return;
		}
		
		for (String member:members){
			try {
				//TODO new session created, will there be any conflicts with the earlier created session to send poll?
				sender = session.createSender(map.get(member));
				sender.send(msg);
				UserInterface.addMessage("System: Final meeting time of poll'"+poll_name+"' has been sent to '"+member+"'.");
			} catch (JMSException e) {
				System.err.println("Final meeting time not sent to " + member + "! JMS Exception found.");
			}
			
		}
		
		try {
			cnx.close();
		} catch (JMSException e) {
			System.out.println("JMS Exception found while attempting to close the connection object for '" + poll_name + "'");
			return;
		}
		UserInterface.addMessage("System: Poll '"+poll_name+"' has been closed.");
		
	}
	
	@Override
	public void receivePoll(Poll poll) {
		
		receivedPolls.add(poll);
		UserInterface.addMessage("System: Received new poll '"+poll.getTitle()+"'.");
		
	}
	
	@Override
	public Response createResponse(String poll_name, List<String> possible_times, List<RespType> responses) {
		//Check for null poll name and possible_times and responses
		if (poll_name==null){
			System.err.println("Cannot create response. The response's poll_name is null.");
		}
		
		if (possible_times==null){
			System.err.println("Cannot create response. The response's possible_times is null.");
		}
		
		if (responses==null){
			System.err.println("Cannot create response. The response's responses is null.");
		}
		
		//Check if poll is in received polls. If so, get possible times from there. if not, error.
		boolean pollFound=false;
		for (Poll pIterator: receivedPolls){
			if (pIterator.getTitle().equals(poll_name)){
				pollFound=true;
			}
		}
		if (pollFound==false){
			System.err.println("Cannot create response. The response's poll_name is not in the receivedPoll list.");
		}
		
		
		Response myResponse= new Response(poll_name, name, possible_times, responses);
		return myResponse;
	}
	
	@Override
	public void sendResponse(String poll_name, Response response) {
		
		if (poll_name==null){
			System.err.println("Cannot send response. The response's poll_name is null.");
		}
		
		if (response==null){
			System.err.println("Cannot send response. The response's response is null.");
		}
		
		User.mHandler.sendResponse(poll_name, response);
	}

	@Override
	public QueueSession getSession(String poll_name) {
		if (poll_name == null) {
			return null;
		}
		
		if (openSessions.containsKey(poll_name)) {
			return openSessions.get(poll_name);
		} else {
			return null;
		}
	}

	@Override
	public List<Poll> getPolls() {
		return polls;
	}
	
}
