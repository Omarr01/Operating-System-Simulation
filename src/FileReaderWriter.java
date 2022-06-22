import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileReaderWriter {

	public boolean writeToFile(String x, String y) throws IOException {
		try {
			FileWriter fWriter = new FileWriter(x, true);
			BufferedWriter out = new BufferedWriter(fWriter);
			out.write(y + "\n");
			out.close();
			return true;
		} catch (IOException e) {
			System.out.print(e.getMessage());
			return false;
		}
	}

	public Object readFile(String x) throws IOException {
		try {
			File program1 = new File(x);
			BufferedReader br = new BufferedReader(new FileReader(program1));
			String st;
			String tmp = "";
			while ((st = br.readLine()) != null) {
				tmp += st;
			}
			return tmp;
		} catch (Exception e) {
			return "Error";
		}

	}

}
