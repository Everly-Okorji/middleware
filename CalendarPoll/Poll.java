import java.util.Set;


public interface Poll {

	void addAMember(String member);

	void setMembers(Set<String> members);
	
	void setOpen();
	
	void setFinalized();
	
	String getTitle();

}
