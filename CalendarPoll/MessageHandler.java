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
	
}
