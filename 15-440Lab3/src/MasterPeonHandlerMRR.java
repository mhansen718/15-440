import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;


public class MasterPeonHandlerMRR implements Runnable {

	private MasterMRR master;
	private Peon peon;
	
	public MasterPeonHandlerMRR(MasterMRR master, Peon peon) {
		super();
		this.master = master;
		this.peon = peon;
	}
	
	@Override
	public void run() {
		InetAddress participantAddress = null;
		int tasksToDo = 0;
		HashMap<TaskID, TaskEntry> tasks = new HashMap<TaskID, TaskEntry>();
		ParticipantStatus peonStatus = null;
		int step = 0;
		int length = 0;
		int mod = 0;
		int modCount = 0;
		
		if (this.peon.dead == 0) {
			/* This participant is very dead, try to revive it */
			try {
				participantAddress = InetAddress.getByName(this.peon.host);
			} catch (UnknownHostException excpt) {
				/* No luck reaching the host, report the error but not much we can do */
				System.out.println(" MasterMRR: Warning: host " + this.peon.host + " could not be ressolved");
				return;
			}
			try {
				if (participantAddress.isReachable(1000)) {
					System.out.println("Remote Starting on " + this.peon.host);
					Runtime.getRuntime().exec("./ssh_work " + this.master.getUsername() + " " + this.peon.host + " " + 
							System.getProperty("user.dir") + " " + InetAddress.getLocalHost().getHostName() + " " + 
							Integer.toString(this.master.getListenPort()));
					// TODO: Reconnect to participant and see what his status is
				}
			} catch (IOException excpt) {
				/* Had a problem doing the reachability test, not much we can do here... */
			}
		} else {
			/* Send the participant all the tasks it should do  and add them to the list of tasks being done by peon */
			tasksToDo = (this.master.getAvailableTasks() / this.master.getCurrentPower()) * this.peon.power;
			
			for (int i = 0; i < tasksToDo; i++) {
				TaskEntry te = this.master.getTask();
				tasks.put(te.id, te);
			}
			
			peon.runningTasks.putAll(tasks);
			// TODO: Ship this list off the to participant
			
			// TODO: Get back a status class thingy
			
			if (peonStatus != null) {
				/* Update the system based on the status from the participant */
				peon.power = peonStatus.power;
				for (TaskID id : peonStatus.completedTasks) {
					peon.runningTasks.remove(id);
					/* Update the job's lists */
					JobEntry j = this.master.findJob(id.jobID);
					j.runningTasks.remove(id);
					j.completeTasks.add(id);
				}
				for (JobEntry j : peonStatus.newJobs) {
					/* Figure out the record partitioning */
					length = j.config.end - j.config.start;
					step = (length / this.master.getCurrentPower());
					mod = (length % this.master.getCurrentPower());
					for (int i = 0; i < this.master.getCurrentPower(); i++) {
						/* Create new tasks and add them to the job and task queue */
						TaskEntry te = new TaskEntry();
						te.config = j.config;
						te.files = new HashSet<String>();
						te.files.add(j.config.inFile);
						te.id = new TaskID();
						te.id.jobID = j.id;
						if (modCount == 0) {
							te.id.end = j.config.start + ((i + 1) * step) + mod;
							te.id.start = j.config.start + (i * step) + mod;
						} else {
							te.id.end = j.config.start + ((i + 1) * step) + (mod - modCount) + 1;
							te.id.start = j.config.start + (i * step) + (mod - modCount);
							modCount--;
						}
						j.runningTasks.add(te.id);
						this.master.addTask(te);
					}
					this.master.addJob(j);
				}
				// TODO: Send confirmation
			} else {
				/* Participant failed to response, make it a bit more dead, and
				 * if its totally dead, dump its work load onto the pending tasks
				 * queue */
				 peon.dead--;
				 if (peon.dead == 0) {
					 for (TaskEntry te : peon.runningTasks.values()) {
						 this.master.addTask(te);
						 peon.runningTasks.remove(te.id);
					 }
				 }
			}
		}
	}
}
