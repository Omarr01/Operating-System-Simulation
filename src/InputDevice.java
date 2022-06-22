import java.util.Scanner;

public class InputDevice {

	public InputDevice() {

	}

	public Object readInputFromUser() {
		Scanner sc = new Scanner(System.in);
		System.out.println("Please enter a value:");
		Object x = sc.nextLine();
		return x;
	}

}
