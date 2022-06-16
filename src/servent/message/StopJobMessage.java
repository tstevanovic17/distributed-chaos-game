package servent.message;

public class StopJobMessage extends BasicMessage {

    private static final long serialVersionUID = 12435465733444662L;

    private String jobName;

    public StopJobMessage(
            int senderPort,
            String senderIpAddress,
            int receiverPort,
            String receiverIpAddress,
            String jobName
    ) {
        super(MessageType.STOP_JOB, senderPort, senderIpAddress, receiverPort, receiverIpAddress, "");
        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }
}
