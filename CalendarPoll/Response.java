import java.util.List;


public class Response {

	public enum RespType {YES, NO, MAYBE};
	
	String poll_name;
	String replier;
	
	List<String> possible_times;
	List<RespType> responses;
	
	Response(String poll_name, String replier, List<String> possible_times, List<RespType> responses) {
		// TODO
	}

}
