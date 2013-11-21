package classic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyPoll implements Poll {
	
	String title;
	String message;
	String creator;
	Status status;
	
	List<String> possible_times;
	Set<String> members;
	
	List<Response> responses;
	
	MyPoll(String title, String message, String creator, Status status, List<String> possibleTimes) {
		this.title=title;
		this.message=message;
		this.creator=creator;
		this.status=status;
		this.possible_times=possibleTimes;
		this.members=new HashSet<String>();
		this.responses = new ArrayList<Response>();
	}

	@Override
	public void addAMember(String member) {
		this.members.add(member);		
	}
	
	@Override
	public void setMembers(Set<String> members) {
		this.members=members;		
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
		
		if (!title.equals(response.poll_name)) {
			System.err.println("Response name '" + response.poll_name + "' does not match poll '" + title + "' !");
			return;
		}
		
		for (Response r: responses) {
			if (r.replier.equals(response.replier)) {
				System.err.println("Response to poll '" + title + "' already received from the client '" + r.replier + "'");
				return;
			}
		}
		
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

	
}
