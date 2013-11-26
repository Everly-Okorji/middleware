package classic;

import java.io.Serializable;
import java.util.List;
import java.util.Set;


public interface Poll extends Serializable {
	
	/**
	 * Status of the poll, possible values are INITIALIZED, OPEN and FINALIZED
	 */
	enum Status {INITIALIZED, OPEN, FINALIZED};
	
	/**
	 * Get the members involved in this poll and store the Set of members
	 * @param members
	 */
	void setMembers(Set<String> members);
	
	/**
	 * Set the status of this poll as OPEN
	 */
	void setOpen();
	
	/**
	 * Set the status of this poll as FINALIZED
	 */
	void setFinalized();
	
	/**
	 * Receive a response and add it to the response list stored in this poll
	 * @param response
	 */
	void addResponse(Response response);
	
	/**
	 * Get the title of this poll, i.e. the poll name
	 * @return poll_name
	 */
	String getTitle();

	/**
	 * Get the status of this poll
	 * @return poll_status
	 */
	Status getStatus();
	
	/**
	 * Get the Set of members involved in this poll
	 * @return Set of members
	 */
	Set<String> getMembers();
	
	/**
	 * Get the possible meeting times specified by the poll creator
	 * @return List of possible meeting times
	 */
	List<String> getMeetingTimes();
	
	/**
	 * Get the received responses to this poll
	 * @return List of received responses
	 */
	List<Response> getResponses();

}
