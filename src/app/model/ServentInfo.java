package app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This is an immutable class that holds all the information for a servent.
 *
 * @author bmilojkovic
 */
public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;

	private int id;
	private final String ipAddress;
	private final int listenerPort;
	private int weakFailureLimit;
	private int strongFailureLimit;
	private List<Job> jobs;

	//todo provjeri za cord id i fifo listener
	//ovo kord je redni broj prikljucivanja
	//vjerovatno ce i nama nesto slicno trebati u klasi identicnoj chord state

	public ServentInfo(String ipAddress, int listenerPort) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;
		this.weakFailureLimit = 1000;
		this.strongFailureLimit = 1000;
		this.jobs = new ArrayList<>();

		this.id = -1;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getWeakFailureLimit() {
		return weakFailureLimit;
	}

	public int getStrongFailureLimit() {
		return strongFailureLimit;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void setWeakFailureLimit(int weakFailureLimit) {
		this.weakFailureLimit = weakFailureLimit;
	}

	public void setStrongFailureLimit(int strongFailureLimit) {
		this.strongFailureLimit = strongFailureLimit;
	}

	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}

	public void addNewJob(Job job) {
		if (!jobs.contains(job)) {
			jobs.add(job);
		}
	}

	public Job getJobByName(String name) {
		for (Job job: jobs) {
			if (job.getName().equals(name)) {
				return job;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "[" + id + "|" + ipAddress + "|" + listenerPort + "]";
	}
}
