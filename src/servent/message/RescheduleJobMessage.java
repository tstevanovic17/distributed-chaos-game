package servent.message;

import app.JobScheduler;

public class RescheduleJobMessage extends BasicMessage {

    private static final long serialVersionUID = 12435423653322334L;

    private JobScheduler.JobScheduleReason scheduleType;

    public RescheduleJobMessage(
            int senderPort,
            String senderIpAddress,
            int receiverPort,
            String receiverIpAddress,
            JobScheduler.JobScheduleReason scheduleType
    ) {
        super(MessageType.RESCHEDULE_JOBS, senderPort, senderIpAddress, receiverPort, receiverIpAddress, "");
        this.scheduleType = scheduleType;
    }

    public JobScheduler.JobScheduleReason getScheduleType() { return scheduleType; }

}
