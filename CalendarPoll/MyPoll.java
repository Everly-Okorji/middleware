import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyPoll implements Poll {

	enum Status {INITIALIZED, OPEN, FINALIZED};
	
	String title;
	String message;
	String creator;
	Status status;
	
	List<String> possible_times;
	Set<String> members;
	
	MyPoll(String title, String message, String creator, Status status, List<String> possibleTimes) {
		this.title=title;
		this.message=message;
		this.creator=creator;
		this.status=status;
		this.possible_times=possibleTimes;
		this.members=new HashSet<String>();
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

	
}
