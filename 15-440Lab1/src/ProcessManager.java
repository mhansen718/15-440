
public class ProcessManager {

	private volatile boolean suspendMe;
	
	public static void main(String[] args) {
		ProcessManager k = new ProcessManager();
		
		k.run();
	}
	
	public void run() {
		if (!suspendMe) {
			System.out.println("FALSE");
		}
		else {
			System.out.println("TRUE");
		}
	}
	
}
