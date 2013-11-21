import java.io.Serializable;
import java.util.Set;


public interface Poll extends Serializable {

	void addAMember(String member);

	void setMembers(Set<String> members);
	
	void setOpen();
	
	void setFinalized();
	
	void addResponse(Response response);
	
	String getTitle();

}
