import java.util.List;


public interface Poll {

	void addAMember(String member);

	void setMembers(List<String> members);
	
	void setOpen();
	
	void setFinalized();

}
