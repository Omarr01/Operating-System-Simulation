
public class ProcessBoundaries {

	int processID;
	int startIndex;
	int endIndex;
	boolean inMemory;

	public ProcessBoundaries(int processID, int startingIndex,int endIndex, boolean inMemory) {
		this.processID = processID;
		this.startIndex = startingIndex;
		this.endIndex = endIndex;
		this.inMemory = inMemory;
	}
	
	public String toString() {
		return processID + " " + startIndex + " " + endIndex + " " + inMemory;
	}
}
