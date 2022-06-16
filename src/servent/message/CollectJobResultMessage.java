package servent.message;

import app.model.Point;

import java.util.List;

public class CollectJobResultMessage extends BasicMessage {

    private static final long serialVersionUID = -2961633943823731472L;

    private final String jobName;
    private final List<Point> comuptedPoints;
    private final List<Integer> jobServentsIds;
    private final int resultRequestingServentId;
    private final String fractalId;

    //dodali smo listu cvorova koji su vezani za posao
    //dodali smo id cvora koji je zatrazio rezultat

    public CollectJobResultMessage(
            int senderPort,
            String senderIpAddress,
            int receiverPort,
            String receiverIpAddress,
            String jobName,
            List<Integer> jobServentsIds,
            int resultRequestingServentId,
            List<Point> comuptedPoints,
            String fractalId
    ) {
        super(MessageType.COLLECT_JOB_RESULT, senderPort, senderIpAddress, receiverPort, receiverIpAddress, "");

        this.jobName = jobName;
        this.comuptedPoints = comuptedPoints;
        this.jobServentsIds = jobServentsIds;
        this.resultRequestingServentId = resultRequestingServentId;
        this.fractalId = fractalId;
    }

    public String getJobName() {
        return jobName;
    }

    public List<Point> getComuptedPoints() {
        return comuptedPoints;
    }

    public List<Integer> getJobServentsIds() {
        return jobServentsIds;
    }

    public int getResultRequestingServentId() {
        return resultRequestingServentId;
    }

    public String getFractalId() {
        return fractalId;
    }
}
