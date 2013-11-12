
public interface MessageHandler {

	/**
	 * Create new thread that listens for messages regarding the specified poll
	 * @param poll_name
	 */
	void receiveMessage(String poll_name);
	
	/**
	 * Add a new temporary queue associated with a new poll
	 * @param poll_name
	 * @param tempQueue
	 */
	void addNewListener(String poll_name, TemporaryQueue tempQueue);
	
}
