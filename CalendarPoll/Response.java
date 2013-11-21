import java.util.List;


public class Response {

	public enum RespType {YES, NO, MAYBE};
	
	String poll_name;
	String replier;
	
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
