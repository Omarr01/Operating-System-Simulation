
public class MemoryObj {
	Object variable;
	Object value;
	
	public MemoryObj(Object variable, Object value) {
		this.variable = variable;
		this.value = value;
	}
	
	public String toString() {
		return  variable + " " + value;
	}
}
