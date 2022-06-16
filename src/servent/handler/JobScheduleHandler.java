package servent.handler;

import app.AppConfig;
import app.JobScheduler;
import servent.message.JobScheduleMessage;
import servent.message.Message;
import servent.message.MessageType;

public class JobScheduleHandler implements MessageHandler {

    private Message clientMessage;

    public JobScheduleHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.JOB_SCHEDULE) {
            AppConfig.timestampedErrorPrint("Job schedule handler got a message that is not JOB_SCHEDULE");
            return;
        }

        JobScheduleMessage jobScheduleMessage = (JobScheduleMessage) clientMessage;
        JobScheduler.JobScheduleReason scheduleType = jobScheduleMessage.getScheduleType();


        int serventCount = AppConfig.systemState.getServentInfoMap().size();
        JobScheduler.scheduleJob(serventCount, scheduleType);
    }
}
