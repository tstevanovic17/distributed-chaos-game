package servent.message;

import app.JobScheduler;
import app.model.Fractal;
import app.model.Job;
import app.model.Point;

import java.util.List;
import java.util.Map;

public class ExecuteJobMessage extends BasicMessage {

    private static final long serialVersionUID = -4792250302040560741L;

    private List<Point> startPoints;
    private Job job;
    private Map<Integer, Fractal> serventJobsMap;
    private Map<Fractal, Fractal> mappedFractalsJobs;
    private JobScheduler.JobScheduleReason scheduleType;
    private int jobSchedulerId;
    private Fractal fractal;

    public ExecuteJobMessage(
            int senderPort,
            String senderIpAddress,
            int receiverPort,
            String receiverIpAddress,
            Fractal fractal,
            List<Point> startPoints,
            Job job,
            Map<Integer, Fractal> serventJobsMap,
            Map<Fractal, Fractal> mappedFractalsJobs,
            JobScheduler.JobScheduleReason scheduleType,
            int jobSchedulerId
    ) {
        super(MessageType.EXECUTE_JOB, senderPort, senderIpAddress, receiverPort, receiverIpAddress, "");
        this.fractal = fractal;
        this.startPoints = startPoints;
        this.job = job;
        this.serventJobsMap = serventJobsMap;
        this.mappedFractalsJobs = mappedFractalsJobs;
        this.scheduleType = scheduleType;
        this.jobSchedulerId = jobSchedulerId;
    }

    public List<Point> getStartPoints() {
        return startPoints;
    }

    public Job getJob() {
        return job;
    }

    public Map<Integer, Fractal> getServentJobsMap() {
        return serventJobsMap;
    }

    public Map<Fractal, Fractal> getMappedFractalsJobs() {
        return mappedFractalsJobs;
    }

    public JobScheduler.JobScheduleReason getScheduleType() { return scheduleType; }

    public int getJobSchedulerId() { return jobSchedulerId; }

    public Fractal getFractal() {
        return fractal;
    }
}
