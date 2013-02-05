import java.io.IOException;


public class SlaveListener implements Runnable {

	private ProcessManager manager;
	
	public SlaveListener(ProcessManager manager) {
		super();
		this.manager = manager;
	}
	
	public void run() {
		while (true) {
			if (manager.inputSafe()) {
				try {
					manager.writeInput(manager.getIn().readLine());
				} catch (IOException excpt) {
					System.out.println("Error: Failed to commune with master; Exiting...");
					return;
				}
			}
		}
	}
}
