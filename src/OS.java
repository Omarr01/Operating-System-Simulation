
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class OS {
	Interpreter i;
	Scheduler s;
	Mutex userOutput;
	Mutex userInput;
	Mutex file;
	Queue<Process> readyQ;
	Queue<Process> blockedQ;
	Process executingProcess;
	Process lastExecuted;
	InputDevice inputDev;
	Printer printer;
	Memory memory;
	FileReaderWriter fileRW;
	ArrayList<ProcessBoundaries> procBound;
	Disk disk;
	final int timeSlice = 2;
	
	public OS() {
		s = new Scheduler(timeSlice, this);
		i = new Interpreter();
		userOutput = new Mutex();
		userInput = new Mutex();
		file = new Mutex();
		blockedQ = new LinkedList<>();
		readyQ = new LinkedList<>();
		inputDev = new InputDevice();
		printer = new Printer();
		memory = new Memory();
		fileRW = new FileReaderWriter();
		this.disk = new Disk();
		this.procBound = new ArrayList<>();
	}

	public void terminateProcess(Process p) {
		System.out.println("Process " + p.getProcessID() + " has been terminated");
		p.semSignal(this.userInput);
		p.semSignal(this.userOutput);
		p.semSignal(this.file);
		p.setPc(p.getInstructionCount());
	}

	public void displayQueues() {
		System.out.println("--------------------------------------------------------------------------");
		System.out.println();
		System.out.println("Ready Queue:    " + readyQ);
		System.out.println("Blocked Queue : " + blockedQ);
		System.out.println();
		System.out.println("--------------------------------------------------------------------------");
		System.out.println();
	}
	
	public void displayMem() {
		System.out.println("--------------------------- Memory-------------------------------------------");
		for(int i=0;i<40;i++) {
			int help=0;
			if(i>=10)
				System.out.println("word "+i+" | "+this.memory.getMemory()[i]);
			else
				System.out.println("word "+i+"  | "+this.memory.getMemory()[i]);
		}
		System.out.println("--------------------------------------------------------------------------");
		System.out.println();
	}
	

	public Object processHardwareReq(Process p, String hw) {
		switch (hw) {
		case "inputDevice":
			if (userInput.getOwnerID() == p.getProcessID())
				return inputDev;
			else
				return null;
		case "printer":
			if (userOutput.getOwnerID() == p.getProcessID())
				return printer;
			else
				return null;
		case "memory":
			return memory;
		case "disk":
			return disk;
		case "fileReadWrite":
			if (file.getOwnerID() == p.getProcessID())
				return fileRW;
			else
				return null;
		default:
			System.out.println("Error");
			break;
		}
		return null;
	}

	public static void main(String[] args) throws IOException {
		OS os = new OS();

		String program1 = "Programs/Program_1.txt";
		int arrivalTime1 = 0;

		String program2 = "Programs/Program_2.txt";
		int arrivalTime2 = 1;

		String program3 = "Programs/Program_3.txt";
		int arrivalTime3 = 4;
		os.displayMem();
		while (true) {
			System.out.println("Time " + os.s.time + ":");
			os.disk.refreshDisk();
			os.s.proccessArrival(program1, arrivalTime1);
			os.s.proccessArrival(program2, arrivalTime2);
			os.s.proccessArrival(program3, arrivalTime3);
			os.s.clockInterrupt();
			os.displayMem();
			
			// First 8 Cycles (Time 0 to Time 7) And One Extra Cycle To Show Swapping A Process Out Of Disk
			if(os.s.time == 9){
				break;
			}
			/*
			 * If we want to execute all cycles
			 * if(os.s.time < 0) {
			 *     break;
			 * }
			 */
			os.lastExecuted = os.executingProcess;
		}

	}
}
