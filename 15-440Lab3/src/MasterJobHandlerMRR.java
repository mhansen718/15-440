import java.util.HashSet;


public class MasterJobHandlerMRR implements Runnable {

	private MasterMRR master;
	private JobEntry job;
	
	public MasterJobHandlerMRR(MasterMRR master, JobEntry job) {
		super();
		this.master = master;
		this.job = job;
	}

	@Override
	public void run() {
		/* Check if the job is done, if so, send word to the app */
		if ((this.job.runningTasks.size() == 0) && (this.job.completeTasks.size() == 1)) {
			// TODO: Send signal to app !!!!
		} else {
			for (TaskID t1 : this.job.completeTasks) {
				for (TaskID t2 : this.job.completeTasks) {
					if ((t1.end == t2.start) ||) {
						TaskEntry te = new TaskEntry();
						te.files = new HashSet<String>();
						te.files.add(Long.toString(t1.jobID) + ".mrr");
						te.files.add(Long.toString(t2.jobID) + ".mrr");
					}
				}
			}

		}

	}
	
}
