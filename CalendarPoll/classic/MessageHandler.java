package classic;
import javax.jms.JMSException;
import javax.jms.TemporaryQueue;


public interface MessageHandler {
	
	/**
	 * Add a new temporary queue associated with a new poll
	 * @param poll_name
	 * @param tempQueue
	 */
	void addNewListener(String poll_name, TemporaryQueue tempQueue);
	
	/**
	 * Stops listening on the temporary queue associated with the specified
	 * poll name
	 * @param poll_name
	 */
	void stopListening(String poll_name) throws JMSException;

	/**
	 * This method receives any messages (such as a new poll needing a response, or
	 * a finalized message)
	 */
	void receiveMessagesOnMyQueue();
	
	/**
	 * Given a poll name and response object, this method sends the response
	 * to the temporary queue associated with the poll. (NOTE: This is a separate
	 * map from that which associates queues with polls created by me (i.e. the user)).
	 * @param poll_name
	 * @param response
	 */
	void sendResponse(String poll_name, Response response);
}
