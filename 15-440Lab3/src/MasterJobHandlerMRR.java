import java.util.HashSet;
import java.util.Iterator;


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
		Iterator<TaskID> tid1 = null;
		Iterator<TaskID> tid2 = null;
        Socket socket = null;
        ObjectOutputStream out;
		
		/* Check if the job is done or errored, if so, send word to the app */
		if (((this.job.runningTasks.size() == 0) && (this.job.completeTasks.size() == 1)) || (this.job.err != null)) {
			try {
                socket = new Socket(this.job.host,this.job.port);
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(this.job);
            } catch (IOException e) {
                System.err.println("Failed to send job back to client");
            }
            
            try {
                out.close();
                socket.close();
            } catch (IOException e) {
                System.err.println("Failed to close connection with client");
            }
		} else {
			/* Loop through the list of completed tasks and pair up into a new task */
			tid1 = this.job.completeTasks.iterator();
			while (tid1.hasNext()) {
				TaskID t1 = tid1.next();
				tid2 = this.job.completeTasks.iterator();
				while(tid2.hasNext()) {
					TaskID t2 = tid2.next();
					/* If the blocks for records are adjacent, merge tasks and add to task queue and 
					 * job running task list, also remove the two tasks from the job completed list */
					if (t1.isAdjacent(t2)) {
						TaskEntry te = new TaskEntry();
						te.file1 = t1.toFileName();
						te.file2 = t2.toFileName();
						te.id = TaskID.merge(t1, t2);
						te.recordSize = 0;
                        
						this.master.addTask(te);
						this.job.runningTasks.add(te.id);
						this.job.completeTasks.remove(t1);
						this.job.completeTasks.remove(t2);
						
						/* Refresh the iterators now that the structure has been modified */
						tid1 = this.job.completeTasks.iterator();
						tid2 = this.job.completeTasks.iterator();
					}
				}
			}

		}

	}
	
}
