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
		// TODO
	}

	@Override
	public void addMembers(List<String> members) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setOpen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFinalized() {
		// TODO Auto-generated method stub
		
	}
	
}
