public class SlaveListener implements Runnable {

	private ProcessManager manager;
	
	public SlaveListener(ProcessManager manager) {
		super();
		this.manager = manager;
	}
	
	public void run() {
		while (true) {
			try {
				if (manager.inputSafe()) {
					manager.writeInput(manager.getIn().readLine());
					manager.inputSafe();
				}
			} catch (Exception excpt) {
				System.out.println();
				System.out.println("Error: Failed to commune with master; Exiting...");
				return;
			}
		}
	}
}
