import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Disk {
	private MemoryObj[] disk = new MemoryObj[4096];

	public MemoryObj[] getDisk() {
		return disk;
	}

	public void refreshDisk() {
		PrintWriter writer;
		try {
			writer = new PrintWriter("Disk/Disk");
			writer.print("");
			writer.close();
		} catch (FileNotFoundException e) {
		}
		try {
			FileWriter fWriter = new FileWriter("Disk/Disk", true);
			BufferedWriter out = new BufferedWriter(fWriter);
			boolean seenNotNulls = false;
			int lowerboundinmemory = 0;
			for (int i = 0; i < this.getDisk().length; i++) {
				if ((this.getDisk()[i] != null) && (this.getDisk()[i].variable.equals("Lower Bound: "))) {
					lowerboundinmemory = (int) this.getDisk()[i].value;
					if (i >= 10)
						out.write("word " + i + " | " + "Lower Bound: " + (i - 3) + "\n");
					else
						out.write("word " + i + "  | " + "Lower Bound: " + (i - 3) + "\n");

				} else if ((this.getDisk()[i] != null) && (this.getDisk()[i].variable.equals("Upper Bound: "))) {
					int upperbouninmemory = (int) this.getDisk()[i].value;
					if (i >= 10)
						out.write("word " + i + " | " + "Upper Bound: "
								+ (i - 1 - 3 + upperbouninmemory - lowerboundinmemory) + "\n");
					else
						out.write("word " + i + "  | " + "Upper Bound: "
								+ (i - 1 - 3 + upperbouninmemory - lowerboundinmemory) + "\n");

				}

				else {
					if (this.getDisk()[i] == null && !seenNotNulls) {
						if (i >= 10)
							out.write("word " + i + " | " + this.getDisk()[i] + "\n");
						else
							out.write("word " + i + "  | " + this.getDisk()[i] + "\n");

					}
					if (this.getDisk()[i] == null && seenNotNulls) {
						break;
					}
					if (this.getDisk()[i] != null) {
						if (i >= 10)
							out.write("word " + i + " | " + this.getDisk()[i] + "\n");
						else
							out.write("word " + i + "  | " + this.getDisk()[i] + "\n");

						seenNotNulls = true;
					}
				}
			}
			out.close();
		} catch (IOException e) {
		}
	}

	public void deleteFromDisk(ProcessBoundaries pb) {

		System.out.println("---------------------------DiskAction-------------------------------------");
		System.out.println("Process " + pb.processID + " is leaving the Disk");

		for (int i = pb.startIndex; i <= pb.endIndex; i++) {
			disk[i] = null;
		}
		this.refreshDisk();
		Scanner sc = new Scanner(System.in);
		System.out.println("Check the disk, then click 'enter' !");
		String s = sc.nextLine();

	}
}
