package servent.message;

import app.model.Fractal;
import app.model.Job;
import app.model.ServentInfo;

import java.util.List;
import java.util.Map;

public class UpdateMessage extends BasicMessage {

	private static final long serialVersionUID = 3586102505319194978L;

	private final List<ServentInfo> serventList;
	private final Map<Integer, Fractal> serventsJobsMap;
	private final List<Job> jobs;
	private final boolean shouldReply;

	public UpdateMessage(
			int senderPort,
			String senderIp,
			int receiverPort,
			String receiverIp,
			List<ServentInfo> serventList,
			Map<Integer, Fractal> serventsJobsMap,
			List<Job> jobs,
			boolean shouldReply
	) {
		super(MessageType.UPDATE, senderPort, senderIp, receiverPort, receiverIp, "");

		this.serventList = serventList;
		this.serventsJobsMap = serventsJobsMap;
		this.jobs = jobs;
		this.shouldReply = shouldReply;
	}

	public List<ServentInfo> getServentList() {
		return serventList;
	}

	public Map<Integer, Fractal> getServentsJobsMap() {
		return serventsJobsMap;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public boolean shouldReply() {
		return shouldReply;
	}

}
