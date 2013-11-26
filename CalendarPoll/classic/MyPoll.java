package classic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * This class implements the poll
 * @author Zirui
 *
 */
public class MyPoll implements Poll {
	
	String title; //The name of the poll
	String message; //The message (description) of the poll
	String creator; //The creator of the poll
	Status status; //The status of the poll
	
	List<String> possible_times; //Possible meeting times
	Set<String> members; //Involved members in the poll
	
	List<Response> responses; //THe responses received to the poll
	
	/**
	 * Constructor initializes variables 
	 * @param title
	 * @param message
	 * @param creator
	 * @param status
	 * @param possibleTimes
	 */
	MyPoll(String title, String message, String creator, Status status, List<String> possibleTimes) {
		this.title=title;
		this.message=message;
		this.creator=creator;
		this.status=status;
		this.possible_times=possibleTimes;
		this.members=new HashSet<String>();
		this.responses = new ArrayList<Response>();
	}
	
	//Members involved in this poll will be add to a set, so there won't be repeated names
	@Override
	public void setMembers(Set<String> members) {
		this.members.addAll(members);		
	}

	@Override
	public void setOpen() {
		status=Status.OPEN;
		
	}

	@Override
	public void setFinalized() {
		status=Status.FINALIZED;
		
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public void addResponse(Response response) {
	    
		//check if the titles are matched
		if (!title.equals(response.poll_name)) {
			System.err.println("Response name '" + response.poll_name + "' does not match poll '" + title + "' !");
			return;
		}
		
		//check if the poll has already received a response from this client
		for (Response r: responses) {
			if (r.replier.equals(response.replier)) {
				System.err.println("Response to poll '" + title + "' already received from the client '" + r.replier + "'");
				return;
			}
		}
		
		//check if the person who sent the response is in the involved members Set
		if (!members.contains(response.replier)) {
			System.err.println("The replier '" + response.replier+ "' is not in the member list of the poll '"+title+"'!" );
			return;
		}
		
		responses.add(response);
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public Set<String> getMembers() {
		return members;
	}

	@Override
	public List<String> getMeetingTimes() {
		return this.possible_times;
	}

	@Override
	public List<Response> getResponses() {
		return responses;
	}

	
}
