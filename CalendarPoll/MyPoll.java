import java.util.ArrayList;
import java.util.List;

public class MyPoll implements Poll {

	enum Status {INITIALIZED, OPEN, FINALIZED};
	
	String title;
	String message;
	String creator;
	Status status;
	
	List<String> possible_times;
	List<String> members;
	
	MyPoll(String title, String message, String creator, Status status, List<String> possibleTimes) {
		this.title=title;
		this.message=message;
		this.creator=creator;
		this.status=status;
		this.possible_times=possibleTimes;
		this.members=new ArrayList<String>();
	}

	@Override
	public void addAMember(String member) {
		this.members.add(member);		
	}
	
	@Override
	public void setMembers(List<String> members) {
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
	
}
