package classic;

import java.util.List;
import java.util.Set;

import javax.jms.QueueSession;

import classic.Response.RespType;


public interface Client {

	/*--------------------REQUESTER-------------------*/
	
	/**
	 *  Creates a poll object
	 * @param title
	 * @param message
	 * @param possibleTimes
	 */
	void createPoll(String title, String message, List<String> possibleTimes);
	
	/**
	 * Sends poll to members
	 * @param title
	 * @param members
	 */
	void sendPoll(String title, Set<String> members);
	
	/**
	 * Updates poll based on response
	 * @param response
	 */
	void UpdatePollInfo(Response response);
	
	/**
	 * Set poll as finalized and send out message
	 * @param poll_name
	 * @param finalizedMeetingTime
	 */
	void closePoll(String poll_name, String finalizedMeetingTime);
	
	/*------------------ REPLIER ---------------------*/
	
	/**
	 * Receives an open poll (created by another client) from the message handler
	 *  and stores it somewhere
	 * @param poll
	 */
	void receivePoll(Poll poll);
	
	/**
	 * Create the response object and set availability
	 * @param poll_name
	 * @param possible_times
	 * @param responses
	 * @return
	 */
	Response createResponse(String poll_name, List<String> possible_times, List<RespType> responses);
	
	/**
	 * Send the response object back to creator of poll using temporary queue
	 * @param response
	 */
	void sendResponse(String poll_name, Response response);
	
	QueueSession getSession(String poll_name);
}
