import java.util.List;


public class MyClient implements Client {

	String name;
	
	List<Poll> polls;
	
	MyClient (String name) {
		// TODO
	}
	
	@Override
	public List<String> getClientsList() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void createPoll(String title, String message,
			List<String> possibleTimes) {
		// TODO Auto-generated method stub
		
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
