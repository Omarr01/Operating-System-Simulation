import java.util.ArrayList;

public class Memory {
	private MemoryObj[] memory = new MemoryObj[40];

	public MemoryObj[] getMemory() {
		return memory;
	}

	public void insertInMem(OS os, Process p, ProcessBoundaries pb, ArrayList<ArrayList<String>> instr) {
		int startIdx = pb.startIndex;
		int endIdx = pb.endIndex;

		// PCB
		memory[startIdx++] = new MemoryObj("Process ID: ", p.getProcessID());

		// m7tagen yt8yro in other classes
		memory[startIdx++] = new MemoryObj("Process State: ", State.READY);
		memory[startIdx++] = new MemoryObj("Program Counter: ", 0);
		memory[startIdx++] = new MemoryObj("Lower Bound: ", pb.startIndex);
		memory[startIdx++] = new MemoryObj("Upper Bound: ", pb.endIndex);

		// Enough space for 3 variables
		memory[startIdx++] = new MemoryObj("Variable", null);
		memory[startIdx++] = new MemoryObj("Variable", null);
		memory[startIdx++] = new MemoryObj("Variable", null);

		// Insert program's instructions
		int instrIdx = 0;
		for (int i = startIdx; i <= endIdx; i++) {
			memory[i] = new MemoryObj("Instruction " + (instrIdx + 1), instr.get(instrIdx));
			instrIdx++;
		}

		pb.inMemory = true;
	}


	public void deleteMemPartition(int lowerB, int upperB) {
		for (int i = lowerB; i <= upperB; i++) {
			memory[i] = null;
		}
	}

	public void displayMemory() {
		for (int i = 0; i < this.getMemory().length; i++) {
			System.out.println(this.getMemory()[i] + " ");
		}
		System.out.println();
	}

}
