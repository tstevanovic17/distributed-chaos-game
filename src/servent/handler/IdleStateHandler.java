package servent.handler;

import app.AppConfig;
import app.JobScheduler;
import app.model.Fractal;
import app.model.Job;
import app.model.ServentInfo;
import servent.message.AckIdleStateMessage;
import servent.message.IdleStateMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

import java.util.List;
import java.util.Map;

public class IdleStateHandler implements MessageHandler {

    private final Message clientMessage;
    private final Map<Integer, Fractal> serventJobsMap;
    private final Map<Fractal, Fractal> mappedFractalJobs;
    private final List<Job> activeJobs;
    private final JobScheduler.JobScheduleReason scheduleType;
    private final int jobSchedulerId;

    public IdleStateHandler(Message clientMessage) {
        this.clientMessage = clientMessage;

        IdleStateMessage idleMessage = (IdleStateMessage) clientMessage;
        serventJobsMap = idleMessage.getServentJobsMap();
        mappedFractalJobs = idleMessage.getMappedFractalsJobs();
        activeJobs = idleMessage.getActiveJobs();
        scheduleType = idleMessage.getScheduleType();
        jobSchedulerId = idleMessage.getJobSchedulerId();
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.IDLE_STATE) {
            AppConfig.timestampedErrorPrint("Idle handler got a message that is not IDLE");
        } else {

            AppConfig.systemState.setServentsJobsMap(serventJobsMap);

            for (Job i : activeJobs) {
                AppConfig.systemState.addJob(i);
            }

            AppConfig.systemState.resetAfterReceivedComputedPoints();
            AppConfig.timestampedStandardPrint("I am idle");

            //posalji moje podatke ako sam se izvrsavao
            if (AppConfig.systemState.getExecutionJob() != null) {
                ExecuteJobHandler.sendMyCalculatedData(mappedFractalJobs, scheduleType);
                AppConfig.systemState.setWorkingJobInstance(null);
            }

            //posalji ack cvoru koji je zapoceo posao
            ServentInfo intercessorServent = AppConfig.systemState.getServentById(jobSchedulerId);

            AckIdleStateMessage ackIdleStateMessage = new AckIdleStateMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.myServentInfo.getIpAddress(),
                    intercessorServent.getListenerPort(),
                    intercessorServent.getIpAddress()
            );
            MessageUtil.sendMessage(ackIdleStateMessage);
        }
    }

}
