import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
        ObjectOutputStream out = null;
        ObjectInputStream in = null;
        
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
					if (this.master.remoteStart()) {
						System.out.println("Remote Starting on " + this.peon.host);
						Runtime.getRuntime().exec("./ssh_work " + this.master.getUsername() + " " + this.peon.host + " " + 
								System.getProperty("user.dir") + " " + InetAddress.getLocalHost().getHostName() + " " + 
								Integer.toString(this.master.getListenPort()) + " " + Integer.toString(this.master.getLocalListenPort()));
					}
				}
			} catch (IOException excpt) {
				/* Had a problem doing the reachability test, not much we can do here... */
			}
            
            try {
                Thread.sleep(5000);
            } catch (Exception e) {
            
            }
            
            try {
                in = new ObjectInputStream(this.peon.connection.getInputStream());
                peonStatus = (ParticipantStatus) in.readObject();
                in.close();
            } catch (Exception e) {
                
            }
            
            peon.power = peonStatus.power;
            
		} else {
			/* Send the participant all the tasks it should do  and add them to the list of tasks being done by peon */
			tasksToDo = (this.master.getAvailableTasks() / this.master.getCurrentPower()) * this.peon.power;
			
			for (int i = 0; i < tasksToDo; i++) {
				TaskEntry te = this.master.getTask();
				JobEntry j = this.master.findJob(te.id.jobID);
				/* If a job has been terminated, dont send it out to be done */
				if ((j != null) && (j.err == null)) {
					tasks.put(te.id, te);
				}
			}
			
			peon.runningTasks.putAll(tasks);
            peonStatus = new ParticipantStatus();
            peonStatus.newTasks = tasks;
			try {
                out = new ObjectOutputStream(this.peon.connection.getOutputStream());
                out.writeObject(peonStatus);
            } catch (IOException e) {
                peonStatus = null;
            }
			
			try {
                in = new ObjectInputStream(this.peon.connection.getInputStream());
                peonStatus = (ParticipantStatus) in.readObject();
            } catch (Exception e) {
                peonStatus = null;
            }
			
			if (peonStatus != null) {
				/* Update the system based on the status from the participant */
				peon.power = peonStatus.power;
				for (TaskID id : peonStatus.completedTasks) {
					peon.runningTasks.remove(id);
					/* Update the jobs lists, clear up the files if the job is terminated */
					JobEntry j = this.master.findJob(id.jobID);
					if ((j != null) && (j.err == null)) {
						j.runningTasks.remove(id);
						j.completeTasks.add(id);
					} else {
						File f = new File(id.toFileName());
						f.delete();
					}
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
						te.file1 = j.config.inFile;
                        te.file2 = null;
                        te.recordSize = j.config.recordSize;
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
				
                peonStatus.power = 0;
                peonStatus.newJobs = null;
                peonStatus.completedTasks = null;
                
                try {
                    out.writeObject(peonStatus);
                } catch (IOException e) {
                    //Failed to send confirmation, not a huge deal
                }
                
                try {
                    in.close();
                    out.close();
                } catch (IOException e) {
                
                }
                
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
