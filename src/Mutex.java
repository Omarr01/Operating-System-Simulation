import java.util.LinkedList;
import java.util.Queue;

public class Mutex {
	boolean available = true;
	Queue<Process> blockedQueue = new LinkedList<>();
	int ownerID;
	
	public int getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}
	
	
	
	
	
}
