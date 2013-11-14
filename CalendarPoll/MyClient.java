import java.util.ArrayList;
import java.util.List;


public class MyClient implements Client {

	String name;
	
	List<String> clientList;
	
	List<Poll> polls;
	
	MyClient (String name, List<String> clientList) { 
		this.name=name;
		this.clientList=clientList;
		this.polls=new ArrayList<Poll>();
	}

	@Override
	public void createPoll(String title, String message,
			List<String> possibleTimes) {
		Poll p=new MyPoll(title, message, name, MyPoll.Status.INITIALIZED, possibleTimes);
		polls.add(p);
	}
	@Override
	public void sendPoll(String title, List<String> members) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Poll getPollResult(Poll poll) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void UpdatePollInfo(Response response) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void closePoll(String poll_name) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void receivePoll(Poll poll) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Response setAvailability(List<String> availability) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void sendResponse(Response response) {
		// TODO Auto-generated method stub
		
	}
}
