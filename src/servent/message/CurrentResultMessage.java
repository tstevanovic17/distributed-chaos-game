package servent.message;

import app.model.Point;

import java.util.List;

public class CurrentResultMessage extends BasicMessage {

    private static final long serialVersionUID = 6430983248472392877L;

    private final String jobName;
    private final String fractalId;
    private final List<Point> computedPoints;

    public CurrentResultMessage(
            int senderPort,
            String senderIpAddress,
            int receiverPort,
            String receiverIpAddress,
            String jobName,
            String fractalId,
            List<Point> computedPoints
    ) {
        super(MessageType.CURRENT_RESULT, senderPort, senderIpAddress, receiverPort, receiverIpAddress, "");
        this.jobName = jobName;
        this.fractalId = fractalId;
        this.computedPoints = computedPoints;
    }

    public String getJobName() {
        return jobName;
    }

    public String getFractalId() {
        return fractalId;
    }

    public List<Point> getComputedPoints() {
        return computedPoints;
    }

}
