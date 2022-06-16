package servent.handler;

import app.AppConfig;
import app.JobScheduler;
import servent.message.RescheduleJobMessage;
import servent.message.Message;
import servent.message.MessageType;

public class RescheduleJobHandler implements MessageHandler {

    private final Message clientMessage;

    public RescheduleJobHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.RESCHEDULE_JOBS) {
            AppConfig.timestampedErrorPrint("Job schedule handler got a message that is not RESCHEDULE_JOB");
            return;
        }

        RescheduleJobMessage rescheduleJobMessage = (RescheduleJobMessage) clientMessage;
        JobScheduler.JobScheduleReason scheduleType = rescheduleJobMessage.getScheduleType();


        int serventCount = AppConfig.systemState.getServentInfoMap().size();
        JobScheduler.scheduleJob(serventCount, scheduleType);
    }
}
