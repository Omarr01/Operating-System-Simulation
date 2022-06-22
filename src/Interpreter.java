import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Interpreter {

	public ArrayList<ArrayList<String>> readfile(String x,OS os) throws IOException {

		File program1 = new File(x);
		BufferedReader br = new BufferedReader(new FileReader(program1));
		ArrayList<ArrayList<String>> instructions = new ArrayList<ArrayList<String>>();
		String st;
		// Process process = new Process(os);
		while ((st = br.readLine()) != null) {
			String str[] = st.split(" ");
			ArrayList<String> ar = new ArrayList<>(Arrays.asList(str));
			if(ar.get(0).equals("assign")) {
				if(ar.get(2).equals("input")) { // if assign a input
					ArrayList<String> firstInstr = new ArrayList<>();
					firstInstr.add(ar.get(2));
					instructions.add(firstInstr);
					ArrayList<String> secondInstr = new ArrayList<>();
					secondInstr.add(ar.get(0));
					secondInstr.add(ar.get(1));
					secondInstr.add("input");
					instructions.add(secondInstr);
				}
				else if (ar.get(2).equals("readFile")) {
					ArrayList<String> firstInstr = new ArrayList<>();
					firstInstr.add(ar.get(2));
					firstInstr.add(ar.get(3));
					instructions.add(firstInstr);
					ArrayList<String> secondInstr = new ArrayList<>();
					secondInstr.add(ar.get(0));
					secondInstr.add(ar.get(1));
					secondInstr.add("readFile");
					instructions.add(secondInstr);
				}
				else {
					instructions.add(ar);
				}
			}
			else {
				instructions.add(ar);
			}
		}
		return instructions;
	}

	public boolean fetchAndExec(OS os, Process p) throws IOException {	
		if(p.getPc() < p.getInstructionCount()) {
			ArrayList<String> curInstr = new ArrayList<>();
			curInstr = p.getCurrentInstruction();
			String func = curInstr.get(0);
			

			switch (func) {
			 
			case "print":
				p.print(curInstr.get(1));
				break;
			case "printFromTo":
				p.printFromTo(curInstr.get(1), curInstr.get(2));
				break;
			case "writeFile":
				p.writeFile(curInstr.get(1), curInstr.get(2));
				break;
			case "readFile":
				p.readFile(curInstr.get(1));
				break;
			case "input":
				p.readInputFromUser();
				break;
			case "assign":
				p.assign(curInstr.get(1), curInstr.get(2));
				break;

			case "semWait":
				switch (curInstr.get(1)) {
				case "userInput":
					p.semWait(os.userInput);
					break;
				case "userOutput":
					p.semWait(os.userOutput);
					break;
				case "file":
					p.semWait(os.file);
					break;
				}
				break;
				
			case "semSignal":
				switch (curInstr.get(1)) {
				case "userInput":
					p.semSignal(os.userInput);
					break;
				case "userOutput":
					p.semSignal(os.userOutput);
					break;
				case "file":
					p.semSignal(os.file);
					break;
				}
				break;

			default:
				System.out.println("Error");
				break;
			}
			
			if(p.getPc() < p.getInstructionCount() - 1)
				p.setPc(p.getPc() + 1);
			else {
				p.setPc(p.getPc() + 1);
				return true;
			}
			
		}
		return false;
	}

}