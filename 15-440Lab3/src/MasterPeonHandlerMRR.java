import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;


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
        
        System.out.println("Running");
        try {
			participantAddress = InetAddress.getByName(this.peon.host);
		} catch (UnknownHostException excpt) {
			/* No luck reaching the host, report the error but not much we can do */
			System.out.println(" ServerMRR: Warning: host " + this.peon.host + " could not be ressolved");
			return;
		}
        
		if (this.peon.dead == 0) {
			/* This participant is very dead, try to revive it */
			System.out.println("working wrong");
			if (this.master.remoteStart()) {
				try {
					if (participantAddress.isReachable(1000)) {
						System.out.println("Remote Starting on " + this.peon.host);
						Runtime.getRuntime().exec("./ssh_work " + this.master.getUsername() + " " + this.peon.host + " " + 
								System.getProperty("user.dir") + " " + InetAddress.getLocalHost().getHostName() + " " + 
								Integer.toString(this.master.getListenPort()) + " " + Integer.toString(this.master.getLocalListenPort()));
						this.peon.connection = null;
					}
				} catch (IOException excpt) {
					/* Had a problem doing the reachability test, not much we can do here... */
					this.peon.connection = null;
					return;
				}
			}
            
			int timeout = 0;
            do {
            	try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					/* Not much can be done, keep going... */
				}
            	timeout++;
            } while(this.peon.connection == null && timeout < this.master.getTimeout());
            
            if (timeout == this.master.getTimeout()) {
            	return;
            }
            
            System.out.println("Getting Status");
            System.out.println(this.peon.connection.toString());
            try {
                in = new ObjectInputStream(this.peon.connection.getInputStream());
                /* Signal client that it can send the object now */
                out = new ObjectOutputStream(this.peon.connection.getOutputStream());
                peonStatus = (ParticipantStatus) in.readObject();
                in.close();
                out.close();
                this.peon.connection.close();
            } catch (Exception e) {
            	e.printStackTrace();
            	this.peon.connection = null;
                return;
            }
            System.out.println("Got status");
            
            peon.power = peonStatus.power;
            peon.dead = this.master.getRetries();
            this.peon.connection = null;
            
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
			
			/* If there are some tasks, add them to the list of running tasks */
			peon.runningTasks.putAll(tasks);
			
            peonStatus = new ParticipantStatus();
            peonStatus.newTasks = tasks;
            
            /* Wait on reconnect */
            int timeout = 0;
            do {
            	try {
            		Thread.sleep(1000);
            	} catch (InterruptedException e) {
            		/* Not much can be done, keep going... */
            	}
            	timeout++;
            } while(this.peon.connection == null && timeout < this.master.getTimeout());
            
            if (timeout == this.master.getTimeout()) {
            	injurePeon();
            	return;
            }
            
			try {
                out = new ObjectOutputStream(this.peon.connection.getOutputStream());
                in = new ObjectInputStream(this.peon.connection.getInputStream());
                out.writeObject(peonStatus);
                peonStatus = (ParticipantStatus) in.readObject();
            } catch (Exception e) {
            	e.printStackTrace();
                peonStatus = null;
            }
			
			if (peonStatus != null) {
				/* Declare the peon healthy */
				peon.dead = this.master.getRetries();
				/* Update the system based on the status from the participant */
				peon.power = peonStatus.power;
				for (TaskID id : peonStatus.completedTasks) {
					TaskEntry check = peon.runningTasks.remove(id);
					/* Check for resent and ignore if it is a resend */
					if (check == null) {
						continue;
					}
					/* Update the jobs lists, clear up the files if the job is terminated */
					JobEntry j = this.master.findJob(id.jobID);
					if ((j != null) && (j.err == null) && (id.err == null)) {
							j.runningTasks.remove(id);
							j.completeTasks.add(id);
					} else {
						this.master.stopJob(j.id, id.err);
						File f = new File(id.toFileName());
						f.delete();
					}
				}
				for (JobEntry j : peonStatus.newJobs) {
					/* Check for resent new job and ignore if present */
					if (this.master.findJob(j.id) != null) {
						continue;
					}
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
                    in.close();
                    out.close();
                    this.peon.connection.close();
                } catch (IOException e) {
                    //Failed to send confirmation, not a huge deal
                }
                
                /* Terminate connect */
                this.peon.connection = null;
                
			} else {
				injurePeon();
			}
		}
	}
	
	private void injurePeon() {
		/* Participant failed to response, make it a bit more dead, and
		 * if its totally dead, dump its work load onto the pending tasks
		 * queue */
		 this.peon.dead--;
		 if (peon.dead == 0) {
			 for (TaskEntry te : peon.runningTasks.values()) {
				 this.master.addTask(te);
				 this.peon.runningTasks.remove(te.id);
			 }
		 }
	}
}
