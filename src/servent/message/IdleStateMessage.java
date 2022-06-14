package servent.message;

import app.JobScheduler;
import app.model.Fractal;
import app.model.Job;

import java.util.List;
import java.util.Map;

public class IdleStateMessage extends BasicMessage {

    private static final long serialVersionUID = 7591006261115609523L;

    private final Map<Integer, Fractal> serventJobsMap;
    private final Map<Fractal, Fractal> mappedFractalsJobs;
    private final List<Job> activeJobs;
    private final JobScheduler.JobScheduleReason scheduleType;
    private final int jobSchedulerId;

    public IdleStateMessage(
            int senderPort,
            int receiverPort,
            String senderIpAddress,
            String receiverIpAddress,
            Map<Integer, Fractal> serventJobsMap,
            Map<Fractal, Fractal> mappedFractalsJobs,
            List<Job> activeJobs,
            JobScheduler.JobScheduleReason scheduleType,
            int jobSchedulerId
    ) {
        super(MessageType.IDLE_STATE, senderPort, senderIpAddress, receiverPort, receiverIpAddress, "");
        this.serventJobsMap = serventJobsMap;
        this.mappedFractalsJobs = mappedFractalsJobs;
        this.activeJobs = activeJobs;
        this.scheduleType = scheduleType;
        this.jobSchedulerId = jobSchedulerId;
    }

    public Map<Integer, Fractal> getServentJobsMap() { return serventJobsMap; }

    public Map<Fractal, Fractal> getMappedFractalsJobs() { return mappedFractalsJobs; }

    public List<Job> getActiveJobs() { return activeJobs; }

    public JobScheduler.JobScheduleReason getScheduleType() { return scheduleType; }

    public int getJobSchedulerId() { return jobSchedulerId; }
}
