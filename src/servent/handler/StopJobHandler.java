package servent.handler;

import app.AppConfig;
import app.WorkingJobInstance;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.StopJobMessage;

public class StopJobHandler implements MessageHandler {

    private final Message clientMessage;

    public StopJobHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.STOP_JOB) {
            AppConfig.timestampedErrorPrint("Stop job handler got a message that is not STOP_JOB");
        } else {

            StopJobMessage stopJobMessage = (StopJobMessage) clientMessage;
            String job = stopJobMessage.getJobName();

            // if I am doing that job then stop
            WorkingJobInstance jobExecution = AppConfig.systemState.getExecutionJob();
            if (jobExecution != null && jobExecution.getJobName().equals(job)) {
                jobExecution.stop();
                AppConfig.systemState.setWorkingJobInstance(null);
            }

            // remove that job from my servent jobs division map and list of all jobs
            AppConfig.systemState.clearJobFromSystemState(job);
        }
    }

}
