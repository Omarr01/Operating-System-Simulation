
import java.io.IOException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;

public class Scheduler {
	int time = 0;
	int timeSlice;
	OS os;

	public Scheduler(int s, OS os) {
		this.timeSlice = s;
		this.os = os;
	}

	private int diskIndex(Disk d, int neededspace) {

		int count = 0;
		int index = -1;
		;
		for (int i = 0; i < d.getDisk().length; i++) {
			if (d.getDisk()[i] == null) {
				if (count == 0)
					index = i;
				count++;
				if (count == neededspace)
					return index;
			} else {
				count = 0;
			}
		}
		return 0;
	}

	public int indexEnoughSpace(Memory m, int neededSpace) {
		int count = 0;
		int index = -1;
		;
		for (int i = 0; i < m.getMemory().length; i++) {
			if (m.getMemory()[i] == null) {
				if (count == 0)
					index = i;
				count++;
				if (count == neededSpace)
					return index;
			} else {
				count = 0;
			}
		}
		return -1;
	}

	public int getSpaceIdxBefore(int x) {
		int count = x;
		for (int i = x - 1; i >= 0; i--) {
			if (os.memory.getMemory()[i] == null) {
				count--;
			} else
				return count;
		}
		return count;
	}

	public int getSpaceIdxAfter(int x) {
		int count = x;
		for (int i = x + 1; i < os.memory.getMemory().length; i++) {
			if (os.memory.getMemory()[i] == null) {
				count++;
			} else
				return count;
		}
		return count;
	}

	public int swapProcess(Memory memory, int memoryNeeded) {

		int lowerB = -1;
		int upperB = -1;

		if (this.timeSlice != 0) {
			int toBeRemovedProcessID = -1;
			for (int i = 0; i < os.memory.getMemory().length; i++) {
				if (os.memory.getMemory()[i].variable.equals("Process ID: ")
						&& os.lastExecuted.getProcessID() != (int) os.memory.getMemory()[i].value) {
					toBeRemovedProcessID = (int) os.memory.getMemory()[i].value;
					lowerB = (int) os.memory.getMemory()[i + 3].value;
					upperB = (int) os.memory.getMemory()[i + 4].value;
					break;
				}
			}
			int spaceNeededInDisk = upperB - lowerB;

			int startIdxDisk = this.diskIndex(this.os.disk, spaceNeededInDisk);

			ProcessBoundaries pb = null;
			for (int i = 0; i < os.procBound.size(); i++) {
				if (toBeRemovedProcessID == os.procBound.get(i).processID) {
					pb = os.procBound.get(i);
				}
			}

			// Insert Swapped Out Process in Disk And Remove From Memory
			os.s.insertInDisk(pb, startIdxDisk, memory);
			os.memory.deleteMemPartition(lowerB, upperB);

		} else {
			lowerB = os.lastExecuted.getLowerBoundPb();
			upperB = os.lastExecuted.getUpperBoundPb();
			int spaceNeededInDisk = upperB - lowerB;

			int startIdxDisk = this.diskIndex(this.os.disk, spaceNeededInDisk);

			ProcessBoundaries pb = null;
			for (int i = 0; i < os.procBound.size(); i++) {
				if (os.lastExecuted.getProcessID() == os.procBound.get(i).processID) {
					pb = os.procBound.get(i);
				}
			}

			// Insert Swapped Out Process in Disk And Remove From Memory
			os.s.insertInDisk(pb, startIdxDisk, memory);
			os.memory.deleteMemPartition(lowerB, upperB);

		}
		int startIndexNewProcess = indexEnoughSpace(os.memory, memoryNeeded);
		if (startIndexNewProcess != -1) {
			return startIndexNewProcess;
		}
		int count = 0;
		for (int i = 0; i < os.memory.getMemory().length; i++) {
			if (os.memory.getMemory()[i] == null)
				count++;
			else if (count != 0) {
				
				os.memory.getMemory()[i + 3].value = (int) (os.memory.getMemory()[i + 3].value) - count;
				int newLowerBound = (int) os.memory.getMemory()[i + 3].value;
				os.memory.getMemory()[i + 4].value = (int) (os.memory.getMemory()[i + 4].value) - count;
				int newUpperBound = (int) os.memory.getMemory()[i + 4].value;
				
				for (int j = 0; j < (newUpperBound - newLowerBound + 1); j++) {
					os.memory.getMemory()[i + j - count] = os.memory.getMemory()[i + j];
				}
				
				for (int j = 1; j <= count; j++) {
					os.memory.getMemory()[newUpperBound + j] = null;
				}
				for (int k = 0; k < os.procBound.size(); k++) {
					if (os.procBound.get(k).processID == (int) os.memory.getMemory()[i - count].value) {
						os.procBound.get(k).startIndex = newLowerBound;
						os.procBound.get(k).endIndex = newUpperBound;
					}
				}
				i = -1;
				count = 0;
			}
		}
		return indexEnoughSpace(os.memory, memoryNeeded);

//		if (upperB - lowerB + 1 >= memoryNeeded) {
//			return lowerB;
//		} else {
//			if (lowerB == 0) {
//				if (os.memory.getMemory()[upperB + 1] != null) {
//					int nextProcessID = (int) os.memory.getMemory()[upperB + 1].value;
//					ProcessBoundaries pb2 = null;
//					for (int i = 0; i < procBound.size(); i++) {
//						if (nextProcessID == procBound.get(i).processID) {
//							pb2 = procBound.get(i);
//						}
//					}
//
//					int nextProcessLower = pb2.startIndex;
//					int nextProcessUpper = pb2.endIndex;
//
//					for (int i = nextProcessUpper + 2; i >= nextProcessLower + 2; i--) {
//						os.memory.getMemory()[i] = os.memory.getMemory()[i - 2];
//					}
//					os.memory.getMemory()[nextProcessLower] = null;
//					os.memory.getMemory()[nextProcessLower + 1] = null;
//
//					pb2.startIndex = nextProcessLower + 2;
//					pb2.endIndex = nextProcessUpper + 2;
//
//					os.memory.getMemory()[nextProcessLower + 5].value = nextProcessLower + 2;
//					os.memory.getMemory()[nextProcessLower + 6].value = nextProcessUpper + 2;
//
//					return lowerB;
//				} else {
//					return lowerB;
//				}
//			} else {
//				if (lowerB == 17) {
//					return lowerB;
//				} else {
//					return lowerB - 2;
//				}
//			}
//		}
	}

	// checking if there is enough memory

	public void proccessArrival(String filepath, int arrivalTime) throws IOException {
		if (arrivalTime == time) {

			ArrayList<ArrayList<String>> instr = os.i.readfile(filepath, os);
			int memoryNeeded = instr.size() + 8;

			Process p1 = new Process(os);

			// below here

			int memLocation = indexEnoughSpace(this.os.memory, memoryNeeded);
			if (memLocation == -1) {
				memLocation = swapProcess(this.os.memory, memoryNeeded);
			}

			ProcessBoundaries pb = new ProcessBoundaries(p1.getProcessID(), memLocation, memLocation + memoryNeeded - 1,
					true);
			os.procBound.add(pb);

			os.memory.insertInMem(os, p1, pb, instr);

			if (os.executingProcess == null) {
				os.executingProcess = p1;
				p1.setState(State.RUNNING);
			} else {
				os.readyQ.add(p1);
			}

		}

	}

	public Process changeProcessRunning(Process currExec, Queue<Process> readyQ) {

		if (!readyQ.isEmpty()) {
			if (!(currExec.getState().equals(State.BLOCKED)) && !(currExec.getState().equals(State.FINISHED))) {
				readyQ.add(currExec);
				currExec.setState(State.READY);
			}
			Boolean inMem = false;
			Process dispatched = readyQ.poll();
			ProcessBoundaries pb = null;
			for (int i = 0; i < os.procBound.size(); i++) {
				if (dispatched.getProcessID() == os.procBound.get(i).processID) {
					pb = os.procBound.get(i);
					inMem = pb.inMemory;
					break;
				}
			}

			if (currExec.getState() == State.FINISHED) {
				int lowerB = -1;
				int upperB = -1;
				for (int i = 0; i < os.procBound.size(); i++) {
					if (os.executingProcess.getProcessID() == os.procBound.get(i).processID) {
						lowerB = os.procBound.get(i).startIndex;
						upperB = os.procBound.get(i).endIndex;
						os.procBound.get(i).inMemory = false;
						break;
					}
				}
				for (int i = lowerB; i <= upperB; i++) {
					os.memory.getMemory()[i] = null;
				}
			}

			if (!inMem) {
				int startIdx = this.indexEnoughSpace(os.memory, pb.endIndex - pb.startIndex + 1);
				if (startIdx == -1) {
					startIdx = this.swapProcess(os.memory, pb.endIndex - pb.startIndex + 1);//////////////////////////////////////
				}

				os.s.returnProcessToMem(pb, os.disk, startIdx); ///////////////////////////////////////////////////
			}

			dispatched.setState(State.RUNNING);

			return dispatched;
		}

		if (currExec.getState() == State.FINISHED) {
			return null;
		}

		// Same process continue executing, no need to add to Ready Queue
		return currExec;
	}

	public void clockInterrupt() throws IOException {

		if (this.timeSlice == 0) {
			os.executingProcess = changeProcessRunning(os.executingProcess, os.readyQ);
			this.timeSlice = os.timeSlice;
			os.displayQueues();
		}
		if (os.executingProcess != null) {
			System.out.println("Currently executing Process " + os.executingProcess.getProcessID() + " instruction "
					+ (os.executingProcess.getPc() + 1) + " " + os.executingProcess.getCurrentInstruction() + "\n");
		}

		Boolean lastInstrExecuted = false;
		if (os.executingProcess != null) {
			lastInstrExecuted = os.i.fetchAndExec(os, os.executingProcess);
		}

		if (lastInstrExecuted) {
			os.executingProcess.setState(State.FINISHED);
		}

		this.timeSlice--;
		this.time++;
		if (os.executingProcess != null) {
			if (os.executingProcess.getState().equals(State.BLOCKED)
					|| os.executingProcess.getState().equals(State.FINISHED)) {
				this.timeSlice = 0;
			}
		} else {
			this.timeSlice = os.timeSlice;
		}

	}

	public void returnProcessToMem(ProcessBoundaries pb, Disk disk, int startIdxMem) {
		int pos = 0;
		int startIdxMemTmp = startIdxMem;
		for (int i = pb.startIndex; i <= pb.endIndex; i++) {
			if (pos == 3) {
				os.memory.getMemory()[startIdxMem++] = new MemoryObj("Lower Bound: ", startIdxMemTmp);
			} else if (pos == 4) {
				os.memory.getMemory()[startIdxMem++] = new MemoryObj("Upper Bound: ",
						startIdxMemTmp + pb.endIndex - pb.startIndex);
			} else {
				os.memory.getMemory()[startIdxMem++] = disk.getDisk()[i];
			}
			pos++;
		}

		disk.deleteFromDisk(pb);

		pb.endIndex = startIdxMemTmp + pb.endIndex - pb.startIndex;
		pb.startIndex = startIdxMemTmp;
		pb.inMemory = true;
	}

	// pb of memory (to load process segment for mem to disk)
	public void insertInDisk(ProcessBoundaries pb, int startIdx, Memory memory) {

		System.out.println("---------------------------DiskAction-------------------------------------");
		System.out.println("Process " + pb.processID + " is going to Disk");

		int lowerBoundMem = pb.startIndex;
		int upperBoundMem = pb.endIndex;

		int tmp = startIdx;

		for (int i = lowerBoundMem; i <= upperBoundMem; i++) {
			os.disk.getDisk()[startIdx++] = memory.getMemory()[i];
		}

		pb.startIndex = tmp;
		pb.endIndex = startIdx - 1;
		pb.inMemory = false;

		os.disk.refreshDisk();
		Scanner sc = new Scanner(System.in);
		System.out.println("Check the disk, then click 'enter' !");
		String s = sc.nextLine();
	}
}
