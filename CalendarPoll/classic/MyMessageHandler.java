package classic;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MyMessageHandler implements MessageHandler {

	static Context ictx = null; 
	
	Map<String, TemporaryQueue> listeners;
	Map<String, Thread> listenThreads;
	
	BlockingQueue<Response> responses;
	BlockingQueue<Poll> polls;
	
	MyMessageHandler() {
		listeners = new HashMap<String, TemporaryQueue>();
		listenThreads = new HashMap<String, Thread>();
		
		responses = new LinkedBlockingQueue<Response>();
		polls = new LinkedBlockingQueue<Poll>();
		
		// Wait for incoming responses and handle them.
		new Thread(new Runnable() {
			@Override
			public void run() {
				handleResponses();
			}
		}).start();
		
		// Wait for incoming polls and handle them.
		new Thread(new Runnable() {
			@Override
			public void run() {
				handleIncomingPolls();
			}
		}).start();
		
		// TODO Also handle finalized poll messages
	}

	@Override
	public void addNewListener(String poll_name, TemporaryQueue tempQueue) {
		if (poll_name == null) {
			System.err.println("Poll name for listener is null!");
			return;
		}
		if (tempQueue == null) {
			System.err.println("Temporary queue to be added is null!");
			return;
		}
		if (listeners.containsKey(poll_name)) {
			System.err.println("Poll name already has an existing message listener associated with it!");
			return;
		}
		
		listeners.put(poll_name, tempQueue);
		
		receiveMessages(poll_name);
	}
	
	@Override
	public void stopListening(String poll_name) throws JMSException {
		if (!listeners.containsKey(poll_name)) {
			System.out.println("Poll name does not have an associated temporary queue!");
			return;
		}
		if (!listeners.containsKey(poll_name)) {
			System.out.println("Poll name has not started listening to its message queue!");
			return;
		}
		
		// Stop the thread that is listening, and delete the temporary queue
		listenThreads.get(poll_name).stop();
		listeners.get(poll_name).delete();
		
		// Remove the stopped thread and deleted queue from the map
		listenThreads.remove(poll_name);
		listeners.remove(poll_name);
	}
	
	@Override
	public void receiveMessagesOnMyQueue() {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void sendResponse(String poll_name, Response response) {
		// TODO Auto-generated method stub
		
	}
	
	private void receiveMessages(final String poll_name) {
		
		Thread listen_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (listeners.containsKey(poll_name)) {
					try {
						ictx = new InitialContext();
						// Queue queue = (Queue) ictx.lookup("queue");
						QueueConnectionFactory qcf = (QueueConnectionFactory) ictx
								.lookup("qcf");
						ictx.close();

						QueueConnection cnx = qcf.createQueueConnection();
						QueueSession session = cnx.createQueueSession(false,
								Session.AUTO_ACKNOWLEDGE);
						QueueReceiver receiver = session
								.createReceiver(listeners.get(poll_name));

						cnx.start();

						while (true) {
							Message msg = receiver.receive();
							Response resp = (Response) ((ObjectMessage) msg)
									.getObject();
							responses.add(resp);
						}
						
					} catch (NamingException e) {
						System.out.println("Naming exception found while attempting to listen for poll '" + poll_name + "'!");
					} catch (JMSException e) {
						System.out.println("JMS exception found while attempting to listen for poll '" + poll_name + "'!");
					}
				} else {
					System.err
							.println("Poll name is not associated with an addd temporary queue!");
				}
			}
		});
		
		listenThreads.put(poll_name, listen_thread);
		listenThreads.get(poll_name).start();
		
	}
	
	private void handleResponses() {
		
		boolean interruptFound = false;
		while (!interruptFound) {
			try {

				Response resp = responses.take();
				System.out.println("From Message Handler: Poll name is "
						+ resp.poll_name + ", replier is " + resp.replier);
				User.client.UpdatePollInfo(resp);

			} catch (InterruptedException e) {
				System.out
						.println("Blocking queue interrupted while waiting for responses!");
				interruptFound = true;
			}
		}
		
	}
	
	private void handleIncomingPolls() {
		boolean interruptFound = false;
		while (!interruptFound) {
			try {
				Poll poll = polls.take();
				System.out.println("From Message Handler: New Poll received! Poll name is' "
						+ poll.getTitle() + "'.");
				User.client.receivePoll(poll);;

			} catch (InterruptedException e) {
				System.out
						.println("Blocking queue interrupted while waiting for polls!");
				interruptFound = true;
			}
		}
	}


}
