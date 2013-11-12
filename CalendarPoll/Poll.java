import java.util.List;


public interface Poll {

	void addMembers(List<String> members);
	
	void setOpen();
	
	void setFinalized();
	
}
