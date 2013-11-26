package classic;

import java.io.Serializable;
import java.util.List;

/**
 * This class is the implementation of the response to polls
 * @author Zirui
 *
 */
public class Response implements Serializable {
	
	/**
	 * The response to a poll can be YES, NO, or Maybe indicating 
	 * the availability of each possible meeting time
	 */
	public enum RespType {YES, NO, MAYBE};
	
	String poll_name; //The name of the poll the response is sent to
	String replier; //The name of the replier
	
	
	/**
	 * The possible_times and responses are stored separately, but they are one-one mapped
	 */
	List<String> possible_times;
	List<RespType> responses;
	
	Response(String poll_name, String replier, List<String> possible_times, List<RespType> responses) {
		this.poll_name=poll_name;
		this.replier=replier;
		this.possible_times=possible_times;
		this.responses=responses;
	}

	String getPollName(){
		return poll_name;
	}
}
