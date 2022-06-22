import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Process {
	private OS os;
	private static int ID = 1;
	private int ProcessID;
	private Stack<Object> variables;

	public Process(OS os) {
		this.ProcessID = ID;
		ID++;
		this.os = os;
		this.variables = new Stack<>();
	}

	public int getProcessID() {
		return this.ProcessID;
	}

	public int getLowerBoundPb() {
		for (int i = 0; i < os.procBound.size(); i++) {
			if (this.getProcessID() == os.procBound.get(i).processID) {
				return os.procBound.get(i).startIndex;
			}
		}
		return -1;
	}

	public int getUpperBoundPb() {
		for (int i = 0; i < os.procBound.size(); i++) {
			if (this.getProcessID() == os.procBound.get(i).processID) {
				return os.procBound.get(i).endIndex;
			}
		}
		return -1;
	}

	public boolean inMem() {
		for (int i = 0; i < os.procBound.size(); i++) {
			if (this.getProcessID() == os.procBound.get(i).processID) {
				return os.procBound.get(i).inMemory;
			}
		}
		return false;
	}

	public int getPc() {
		int pc = -1;
		if (this.inMem()) {
			Memory mem = (Memory) this.requestHardware("memory");
			if (mem != null) {
				int lowerB = this.getLowerBoundPb();
				MemoryObj memObj = (MemoryObj) os.memory.getMemory()[lowerB + 2];
				pc = (int) memObj.value;
			} else {
				os.terminateProcess(this);
			}
		} else {
			Disk disk = (Disk) this.requestHardware("disk");
			if (disk != null) {
				int lowerB = this.getLowerBoundPb();
				MemoryObj memObj = (MemoryObj) os.disk.getDisk()[lowerB + 2];
				pc = (int) memObj.value;
			} else {
				os.terminateProcess(this);
			}
		}
		return pc;
	}

	public void setPc(int pc) {
		if (this.inMem()) {
			Memory mem = (Memory) this.requestHardware("memory");
			if (mem != null) {
				int lowerB = this.getLowerBoundPb();
				os.memory.getMemory()[lowerB + 2].value = pc;
			} else {
				os.terminateProcess(this);
			}
		} else {
			Disk disk = (Disk) this.requestHardware("disk");
			if (disk != null) {
				int lowerB = this.getLowerBoundPb();
				os.disk.getDisk()[lowerB + 2].value = pc;
			} else {
				os.terminateProcess(this);
			}
		}
	}

	public State getState() {
		State state = null;
		if (this.inMem()) {
			Memory mem = (Memory) this.requestHardware("memory");
			if (mem != null) {
				int lowerB = this.getLowerBoundPb();
				MemoryObj memObj = (MemoryObj) os.memory.getMemory()[lowerB + 1];
				state = (State) memObj.value;
			} else {
				os.terminateProcess(this);
			}
		}

		else {
			Disk disk = (Disk) this.requestHardware("disk");
			if (disk != null) {
				int lowerB = this.getLowerBoundPb();
				MemoryObj memObj = (MemoryObj) os.disk.getDisk()[lowerB + 1];
				state = (State) memObj.value;
			} else {
				os.terminateProcess(this);
			}
		}
		return state;
	}

	public void setState(State state) {
		if (this.inMem()) {
			Memory mem = (Memory) this.requestHardware("memory");
			if (mem != null) {
				int lowerB = this.getLowerBoundPb();
				os.memory.getMemory()[lowerB + 1].value = state;
			} else {
				os.terminateProcess(this);
			}
		} else {
			Disk disk = (Disk) this.requestHardware("disk");
			if (disk != null) {
				int lowerB = this.getLowerBoundPb();
				os.disk.getDisk()[lowerB + 1].value = state;
			} else {
				os.terminateProcess(this);
			}
		}
	}

	public int getInstructionCount() {
		if(this.inMem()) {
			Memory mem = (Memory) this.requestHardware("memory");
			if (mem != null) {
				int lowerB = this.getLowerBoundPb();
				int upperB = this.getUpperBoundPb();
				return upperB - (lowerB + 8) + 1;
			} else {
				os.terminateProcess(this);
			}
		}
		return -1;
	}

	public ArrayList<String> getCurrentInstruction() {
		int pc = this.getPc();
		if (this.inMem()) {
			Memory mem = (Memory) this.requestHardware("memory");
			if (mem != null) {
				int lowerB = this.getLowerBoundPb();
				return (ArrayList<String>) os.memory.getMemory()[lowerB + 8 + pc].value;
			} else {
				os.terminateProcess(this);
			}
		} else {
			Disk disk = (Disk) this.requestHardware("disk");
			if (disk != null) {
				int lowerB = this.getLowerBoundPb();
				return (ArrayList<String>) os.disk.getDisk()[lowerB + 8 + pc].value;
			} else {
				os.terminateProcess(this);
			}

		}
		return null;
	}

	public Object requestHardware(String hw) {
		return os.processHardwareReq(this, hw);
	}

	public void semWait(Mutex m) {
		if (m.available == true) {
			m.setOwnerID(this.getProcessID());
			m.available = false;
		} else {
			this.setState(State.BLOCKED);
			m.blockedQueue.add(this);
			os.blockedQ.add(this);
		}

	}

	public void semSignal(Mutex m) {
		if (m.ownerID == this.getProcessID()) {
			if (m.blockedQueue.isEmpty())
				m.available = true;
			else {
				Process p = m.blockedQueue.remove();
				os.blockedQ.remove(p);
				p.setState(State.READY);
				os.readyQ.add(p);
				m.setOwnerID(p.getProcessID());
			}
		}
	}

	public String toString() {
		return ("Process " + this.ProcessID + " is " + this.getState());
	}

	public void print(Object x) {
		Object n = null;
		Memory mem = (Memory) this.requestHardware("memory");
		if (mem != null) {
			int lowerB = this.getLowerBoundPb();

			Object var1 = os.memory.getMemory()[lowerB + 5].variable;
			Object var2 = os.memory.getMemory()[lowerB + 6].variable;
			Object var3 = os.memory.getMemory()[lowerB + 7].variable;

			if (x.equals(var1)) {
				n = os.memory.getMemory()[lowerB + 5].value;
			} else if (x.equals(var2)) {
				n = os.memory.getMemory()[lowerB + 6].value;
			} else if (x.equals(var3)) {
				n = os.memory.getMemory()[lowerB + 7].value;
			}

		} else {
			os.terminateProcess(this);
		}

		Printer p = (Printer) this.requestHardware("printer");
		if (p != null) {
			if (n != null) {
				p.print(n);
			} else {
				p.print(x);
			}
		} else {
			os.terminateProcess(this);
		}
	}

	public void printFromTo(Object x, Object y) {
		int n1 = 0;
		int n2 = 0;
		try {
			n1 = Integer.parseInt(x.toString());
		} catch (Exception e) {
			Memory mem = (Memory) this.requestHardware("memory");
			if (mem != null) {
				boolean found = false;

				int lowerB = this.getLowerBoundPb();

				Object var1 = os.memory.getMemory()[lowerB + 5].variable;
				Object var2 = os.memory.getMemory()[lowerB + 6].variable;
				Object var3 = os.memory.getMemory()[lowerB + 7].variable;

				if (x.equals(var1)) {
					found = true;
					try {
						n1 = Integer.parseInt(os.memory.getMemory()[lowerB + 5].value.toString());
					} catch (Exception e1) {
						os.terminateProcess(this);
						return;
					}

				} else if (x.equals(var2)) {
					found = true;
					try {
						n1 = Integer.parseInt(os.memory.getMemory()[lowerB + 6].value.toString());
					} catch (Exception e1) {
						os.terminateProcess(this);
						return;
					}
				} else if (x.equals(var3)) {
					found = true;
					try {
						n1 = Integer.parseInt(os.memory.getMemory()[lowerB + 7].value.toString());
					} catch (Exception e1) {
						os.terminateProcess(this);
						return;
					}
				}
				if (!found) {
					os.terminateProcess(this);
					return;
				}
			} else {
				os.terminateProcess(this);
			}
		}

		try {
			n2 = Integer.parseInt(y.toString());
		} catch (Exception e) {
			Memory mem = (Memory) this.requestHardware("memory");
			if (mem != null) {
				boolean found = false;

				int lowerB = this.getLowerBoundPb();

				Object var1 = os.memory.getMemory()[lowerB + 5].variable;
				Object var2 = os.memory.getMemory()[lowerB + 6].variable;
				Object var3 = os.memory.getMemory()[lowerB + 7].variable;

				if (y.equals(var1)) {
					found = true;
					try {
						n2 = Integer.parseInt(os.memory.getMemory()[lowerB + 5].value.toString());
					} catch (Exception e1) {
						os.terminateProcess(this);
						return;
					}

				} else if (y.equals(var2)) {
					found = true;
					try {
						n2 = Integer.parseInt(os.memory.getMemory()[lowerB + 6].value.toString());
					} catch (Exception e1) {
						os.terminateProcess(this);
						return;
					}
				} else if (y.equals(var3)) {
					found = true;
					try {
						n2 = Integer.parseInt(os.memory.getMemory()[lowerB + 7].value.toString());
					} catch (Exception e1) {
						os.terminateProcess(this);
						return;
					}
				}
				if (!found) {
					os.terminateProcess(this);
					return;
				}
			} else {
				os.terminateProcess(this);
			}
		}

		Printer p = (Printer) this.requestHardware("printer");
		if (p != null)
			for (int i = n1; i <= n2; i++) {
				p.print(i);
			}
		else {
			os.terminateProcess(this);
		}
	}

	public void writeFile(String x, String y) throws IOException {
		Memory mem = (Memory) this.requestHardware("memory");
		Object path = null;
		Object data = null;
		if (mem != null) {

			int lowerB = this.getLowerBoundPb();

			Object var1 = os.memory.getMemory()[lowerB + 5].variable;
			Object var2 = os.memory.getMemory()[lowerB + 6].variable;
			Object var3 = os.memory.getMemory()[lowerB + 7].variable;

			if (x.equals(var1)) {
				path = os.memory.getMemory()[lowerB + 5].value;
			} else if (x.equals(var2)) {
				path = os.memory.getMemory()[lowerB + 6].value;
			} else if (x.equals(var3)) {
				path = os.memory.getMemory()[lowerB + 7].value;
			}

			if (y.equals(var1)) {
				data = os.memory.getMemory()[lowerB + 5].value;
			} else if (y.equals(var2)) {
				data = os.memory.getMemory()[lowerB + 6].value;
			} else if (y.equals(var3)) {
				data = os.memory.getMemory()[lowerB + 7].value;
			}

		} else {
			os.terminateProcess(this);
		}

		FileReaderWriter fRW = (FileReaderWriter) this.requestHardware("fileReadWrite");

		if (fRW != null) {
			if (path == null) {
				path = x;
			}
			if (data == null) {
				data = y;
			}
			Boolean success = fRW.writeToFile(path.toString(), data.toString());
			if (!success) {
				os.terminateProcess(this);
			}
		} else {
			os.terminateProcess(this);
		}
	}

	public void readFile(String x) throws IOException {
		Memory mem = (Memory) this.requestHardware("memory");
		Object path = null;
		if (mem != null) {

			int lowerB = this.getLowerBoundPb();

			Object var1 = os.memory.getMemory()[lowerB + 5].variable;
			Object var2 = os.memory.getMemory()[lowerB + 6].variable;
			Object var3 = os.memory.getMemory()[lowerB + 7].variable;

			if (x.equals(var1)) {
				path = os.memory.getMemory()[lowerB + 5].value;
			} else if (x.equals(var2)) {
				path = os.memory.getMemory()[lowerB + 6].value;
			} else if (x.equals(var3)) {
				path = os.memory.getMemory()[lowerB + 7].value;
			}

		} else {
			os.terminateProcess(this);
		}

		FileReaderWriter fRW = (FileReaderWriter) this.requestHardware("fileReadWrite");
		if (fRW != null) {
			if (path == null) {
				path = x;
			}
			variables.push(fRW.readFile(path.toString()));
		} else {
			os.terminateProcess(this);
		}
	}

	public void readInputFromUser() {
		InputDevice inputDev = (InputDevice) this.requestHardware("inputDevice");
		if (inputDev != null) {
			variables.push(inputDev.readInputFromUser());
		} else {
			os.terminateProcess(this);
		}
	}

	public void assign(Object x, Object y) throws IOException {
		Memory mem = (Memory) this.requestHardware("memory");
		if (mem != null) {
			if (y.equals("input") || y.equals("readFile")) {
				Boolean found = false;

				int lowerB = this.getLowerBoundPb();

				Object var1 = os.memory.getMemory()[lowerB + 5].variable;
				Object var2 = os.memory.getMemory()[lowerB + 6].variable;
				Object var3 = os.memory.getMemory()[lowerB + 7].variable;

				if (x.equals(var1)) {
					os.memory.getMemory()[lowerB + 5].value = variables.pop();
					found = true;
				} else if (x.equals(var2)) {
					os.memory.getMemory()[lowerB + 6].value = variables.pop();
					found = true;
				} else if (x.equals(var3)) {
					os.memory.getMemory()[lowerB + 7].value = variables.pop();
					found = true;
				}

				if (!found) {
					if (var1.equals("Variable")) {
						os.memory.getMemory()[lowerB + 5].variable = x;
						os.memory.getMemory()[lowerB + 5].value = variables.pop();
					} else if (var2.equals("Variable")) {
						os.memory.getMemory()[lowerB + 6].variable = x;
						os.memory.getMemory()[lowerB + 6].value = variables.pop();
					} else if (var3.equals("Variable")) {
						os.memory.getMemory()[lowerB + 7].variable = x;
						os.memory.getMemory()[lowerB + 7].value = variables.pop();
					}
				}

			} else {
				Boolean found = false;

				int lowerB = this.getLowerBoundPb();

				Object var1 = os.memory.getMemory()[lowerB + 5].variable;
				Object var2 = os.memory.getMemory()[lowerB + 6].variable;
				Object var3 = os.memory.getMemory()[lowerB + 7].variable;

				if (x.equals(var1)) {
					os.memory.getMemory()[lowerB + 5].value = y;
					found = true;
				} else if (x.equals(var2)) {
					os.memory.getMemory()[lowerB + 6].value = y;
					found = true;
				} else if (x.equals(var3)) {
					os.memory.getMemory()[lowerB + 7].value = y;
					found = true;
				}

				if (!found) {
					if (var1.equals("Variable")) {
						os.memory.getMemory()[lowerB + 5].variable = x;
						os.memory.getMemory()[lowerB + 5].value = y;
					} else if (var2.equals("Variable")) {
						os.memory.getMemory()[lowerB + 6].variable = x;
						os.memory.getMemory()[lowerB + 6].value = y;
					} else if (var3.equals("Variable")) {
						os.memory.getMemory()[lowerB + 7].variable = x;
						os.memory.getMemory()[lowerB + 7].value = y;
					}
				}
			}
		} else {
			os.terminateProcess(this);
		}
	}

}
