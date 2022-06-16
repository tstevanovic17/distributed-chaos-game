package servent.message;


import app.model.Point;

import java.util.List;

public class JobResultMessage extends BasicMessage {

    private static final long serialVersionUID = 5839429684400309826L;

    private final String jobName;
    private final List<Point> computedPoints;
    private final int width;
    private final int height;
    private final double proportion;
    private final String fractalId;

    public JobResultMessage(
            int senderPort,
            String senderIpAddress,
            int receiverPort,
            String receiverIpAddress,
            String jobName,
            List<Point> computedPoints,
            int width,
            int height,
            double proportion,
            String fractalId
    ) {
        super(MessageType.JOB_RESULT, senderPort, senderIpAddress, receiverPort, receiverIpAddress, "");
        this.jobName = jobName;
        this.computedPoints = computedPoints;
        this.width = width;
        this.height = height;
        this.proportion = proportion;
        this.fractalId = fractalId;
    }


    public String getJobName() {
        return jobName;
    }

    public List<Point> getComputedPoints() {
        return computedPoints;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getProportion() {
        return proportion;
    }

    public String getFractalId() {
        return fractalId;
    }
}
