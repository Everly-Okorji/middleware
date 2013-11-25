package classic;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TemporaryQueue;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class MyMessageHandler implements MessageHandler {

	static Context ictx = null; 
	
	Map<String, TemporaryQueue> listeners;
	Map<String, Thread> listenThreads;

	Map<String, TemporaryQueue> replierQueues;
	
	BlockingQueue<Response> responses;
	BlockingQueue<Poll> polls;
	
	MyMessageHandler() {
		listeners = new HashMap<String, TemporaryQueue>();
		listenThreads = new HashMap<String, Thread>();
		
		replierQueues = new HashMap<String, TemporaryQueue>();
		
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

	}

	@Override
	public void addNewListener(String poll_name, TemporaryQueue tempQueue) {
		if (poll_name == null) {
			System.err.println("MH: Poll name for listener is null!");
			return;
		}
		if (tempQueue == null) {
			System.err.println("MH: Temporary queue to be added to poll '" + poll_name + "' is null!");
			return;
		}
		if (listeners.containsKey(poll_name)) {
			for (String poll: listeners.keySet()) {
				System.out.println("In listeners: " + poll);
			}
			System.err.println("MH: Poll name '" + poll_name + "' already has an existing temporary queue associated with it!");
			return;
		}
		
		listeners.put(poll_name, tempQueue);
		
		receiveResponses(poll_name);
	}
	
	@Override
	public void stopListening(String poll_name) throws JMSException {
		if (!listeners.containsKey(poll_name)) {
			System.out.println("MH: Poll name does not have an associated temporary queue!");
			return;
		}
		if (!listeners.containsKey(poll_name)) {
			System.out.println("MH: Poll name has not started listening to its message queue!");
			return;
		}
		
		// Stop the thread that is listening, and delete the temporary queue
		listenThreads.get(poll_name).stop();
		listeners.get(poll_name).delete();
		
		// Remove the stopped thread and deleted queue from the map
		listenThreads.remove(poll_name);
		listeners.remove(poll_name);
	}
	
	// Listens on the queue dedicated to this user
	@Override
	public void receiveMessagesOnMyQueue() {
		
		try {
			ictx = new InitialContext();
			Queue queue = (Queue) ictx.lookup(User.user);
			System.out.println("JMS Queue was fetched for name '" + User.user + "'!");
			QueueConnectionFactory qcf = (QueueConnectionFactory) ictx
					.lookup("qcf");
			ictx.close();

			QueueConnection cnx = qcf.createQueueConnection();
			QueueSession session = cnx.createQueueSession(false,
					Session.AUTO_ACKNOWLEDGE);
			QueueReceiver receiver = session.createReceiver(queue);

			cnx.start();

			while (true) {
				Message msg = receiver.receive();
				if (msg instanceof TextMessage) {
					UserInterface.addMessage(((TextMessage) msg).getText());
				} else if (msg instanceof ObjectMessage) {
					Poll p = (Poll) ((ObjectMessage) msg).getObject();
					polls.add(p);
					replierQueues.put(p.getTitle(), (TemporaryQueue) msg.getJMSReplyTo());
				} else {
					System.err
							.println("MH: Could not interpret the received message!");
				}
			}

		} catch (NamingException e) {
			System.out.println("MH: Naming exception found while attempting to receive messages!");
		} catch (JMSException e) {
			System.out.println("MH: JMS exception found while attempting to receive messages!");
		}
	 
	}

	// Sends prepared poll responses
	@Override
	public void sendResponse(String poll_name, Response response) {
		
		TemporaryQueue queue = replierQueues.get(poll_name);
		
		if (queue == null) {
			System.err.println("MH: Poll name '" + poll_name + "' is not associated with a temporary queue for sending response!");
			return;
		}
		
		try {
		
		 	ictx = new InitialContext();
		    QueueConnectionFactory qcf = (QueueConnectionFactory) ictx.lookup("qcf");
		    ictx.close();

		    QueueConnection cnx = qcf.createQueueConnection();
		    QueueSession session = cnx.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
		    QueueSender sender = session.createSender(queue);
		    
		      ObjectMessage msg = session.createObjectMessage();
		      msg.setObject(response);
		      sender.send(msg);
		      
		      System.out.println("MH: Response sent for Poll Name '" + poll_name + "'!");

		    cnx.close();
		} catch (NamingException e) {
			System.err.println("MH: Naming exception found while attempting to send response for poll name '" + poll_name + "'!");
		} catch (JMSException e) {
			System.err.println("MH: JMS exception found while attempting to send response for poll name '" + poll_name + "'!");
		}
	}
	
	// Waiting to receive poll responses
	private void receiveResponses(final String poll_name) {
		
		if (listenThreads.containsKey(poll_name)) {
			System.out.println("A listener already exists for '" + poll_name + "'!");
			return;
		}
		
		if (!listeners.containsKey(poll_name)) {
			System.out.println("You are not listening for responses for '" + poll_name + "'!");
			return;
		}
		
		Thread listen_thread = new Thread(new Runnable() {
			@Override
			public void run() {
				if (listeners.containsKey(poll_name)) {
					try {
					/*	ictx = new InitialContext();
						// Queue queue = (Queue) ictx.lookup("queue");
						QueueConnectionFactory qcf = (QueueConnectionFactory) ictx
								.lookup("qcf");
						ictx.close();

						QueueConnection cnx = qcf.createQueueConnection();
						QueueSession session = cnx.createQueueSession(false,
								Session.AUTO_ACKNOWLEDGE);
						*/
						QueueSession session = User.client.getSession(poll_name);
						
						if (session == null) {
							System.err.println("Cannot listen for responses to '" + poll_name + "' because the session associated with this poll is null.");
							return;
						}
						
						TemporaryQueue tempQueue = listeners.get(poll_name);
						
						if (tempQueue == null) {
							System.err.println("Cannot listen for responses to '" + poll_name + "' because the temporary queue associated with this poll is null.");
							return;
						}
						
						QueueReceiver receiver = session
								.createReceiver(tempQueue);

						while (true) {
							Message msg = receiver.receive();
							Response resp = (Response) ((ObjectMessage) msg)
									.getObject();
							responses.add(resp);
						}
						
					} catch (JMSException e) {
						System.err.println("MH: JMS exception found while attempting to listen for poll '" + poll_name + "'. Perhaps the poll has been closed!");
					}
				} else {
					System.err
							.println("MH: Poll name is not associated with an added temporary queue!");
				}
			}
		});
		
		listenThreads.put(poll_name, listen_thread);
		listenThreads.get(poll_name).start();
		
	}
	
	// Handles received poll responses
	private void handleResponses() {
		
		boolean interruptFound = false;
		while (!interruptFound) {
			try {

				Response resp = responses.take();
				System.out.println("MH: Poll name is "
						+ resp.poll_name + ", replier is " + resp.replier);
				User.client.UpdatePollInfo(resp);

			} catch (InterruptedException e) {
				System.err
						.println("MH: Blocking queue interrupted while waiting for responses!");
				interruptFound = true;
			}
		}
		
	}
	
	private void handleIncomingPolls() {
		boolean interruptFound = false;
		while (!interruptFound) {
			try {
				Poll poll = polls.take();
				System.out.println("MH: New Poll received! Poll name is '"
						+ poll.getTitle() + "'.");
				User.client.receivePoll(poll);

			} catch (InterruptedException e) {
				System.err
						.println("MH: Blocking queue interrupted while waiting for polls!");
				interruptFound = true;
			}
		}
	}

}
