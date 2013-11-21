import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.naming.Context;
import javax.naming.InitialContext;


public class MyClient implements Client {

	String name;
	
	List<String> clientList;
	
	List<Poll> polls;
	
	Map<String, Queue> map;
	
	List<String> peopleNames;//need to get input from file!!!
	
	List<Poll> pollMsg;
	
	MessageHandler message;
	
	static Context ictx=null;
	
	MyClient (String name, List<String> clientList) { 
		this.name=name;
		this.clientList=clientList;
		this.polls=new ArrayList<Poll>();
		
		pollMsg=new ArrayList<Poll>();
		map = new HashMap<String, Queue>();
		for (String people: peopleNames){
			ictx = new InitialContext();
		    Queue queue = (Queue) ictx.lookup(people);
		    QueueConnectionFactory qcf = (QueueConnectionFactory) ictx.lookup("qcf");
		    ictx.close();
		    map.put(people, queue);
		}
		
		message=new MyMessageHandler();
				
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
		}
		else{
			for (String people: peopleNames){
				if (members.contains(people)){
					Map.Entry<String, Queue> entry=null;
					boolean peoplefound=false;
					for (Map.Entry<String, Queue> eIterator : map.entrySet()) {
						if (eIterator.getKey().equals(people)){
							peoplefound=true;
							entry=eIterator;
							break;
						}
					}
					if (peoplefound==false){
						System.err.println("There is no such people named:"+people+".");
					}
					else{	
						
						
						QueueConnection cnx = qcf.createQueueConnection();
					    QueueSession session = cnx.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
						
						TemporaryQueue tempQueue = session.createTemporaryQueue();
						QueueSender sender = session.createSender(entry.getValue());
						QueueReceiver receiver = session.createReceiver(tempQueue);
						cnx.start();
						
						ObjectMessage msg=poll;
						msg.setJMSReplyTo(tempQueue);
					    sender.send(msg);
					    pollMsg.add(msg);
					    
					    System.out.println("Poll sent!");
					    //cnx.close();  // when do we need this line???
					    
					}
				}
			}
		}
	}
	
	@Override
	public Poll getPollResult(Poll poll) {
		boolean pollfound=false;
		Poll pollResult=null;
		
		for (Poll pIterator: polls){
			if (pIterator.getTitle().equals(poll.getTitle())) {
				pollResult=pIterator;
				pollfound=true;
				break;
			}
		}
		if (pollfound==false){
			System.err.println("There is no such poll titled:"+poll.getTitle()+".");
			return null;
		}
		else{
			return pollResult;
		}
		
	}
	
	@Override
	public void UpdatePollInfo(Response response) {
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
		message.receiveMessage(poll);
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
