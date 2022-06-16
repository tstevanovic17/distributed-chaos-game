package servent.handler;

import app.AppConfig;
import app.model.Point;
import app.model.ServentInfo;
import servent.message.CollectJobResultMessage;
import servent.message.JobResultMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class CollectJobResultHandler implements MessageHandler {

    private final Message clientMessage;

    public CollectJobResultHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.COLLECT_JOB_RESULT) {
            AppConfig.timestampedErrorPrint("Ask job result handler got a message that is not ASK_JOB_RESULT");
        } else {

            CollectJobResultMessage collectResultMessage = (CollectJobResultMessage) clientMessage;
            String jobName = collectResultMessage.getJobName();
            int resultRequestingServentId = collectResultMessage.getResultRequestingServentId();

            // add my points
            List<Point> jobPointFromMessage = collectResultMessage.getComuptedPoints();
            List<Point> computedPoints = new ArrayList<>(AppConfig.systemState.getExecutionJob().getDrawnPoints());
            jobPointFromMessage.addAll(computedPoints);

            //ako je lista ostalih cvorova prazna, zavrseno je prikupljanje rezultata, saljemo result poruku
            List<Integer> otherJobServents = collectResultMessage.getJobServentsIds();

            if (otherJobServents.isEmpty()) {
                // send result to the node which requested it

                int width = AppConfig.systemState.getExecutionJob().getWidth();
                int height = AppConfig.systemState.getExecutionJob().getHeight();
                double proportion = AppConfig.systemState.getExecutionJob().getProportion();

                ServentInfo resultRequestingServent = AppConfig.systemState.getServentById(resultRequestingServentId);

                JobResultMessage jobResultMessage = new JobResultMessage(
                        AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.myServentInfo.getIpAddress(),
                        resultRequestingServent.getListenerPort(),
                        resultRequestingServent.getIpAddress(),
                        jobName,
                        jobPointFromMessage,
                        width,
                        height,
                        proportion,
                        collectResultMessage.getFractalId()
                );

                MessageUtil.sendMessage(jobResultMessage);
            } else {
                //ako lista nije prazna, uzimamo prvi sledeci cvor iz liste i saljemo nase prikupljene rezultate
                int nextJobServentId = otherJobServents.remove(0);
                ServentInfo nextJobServent = AppConfig.systemState.getServentById(nextJobServentId);

                CollectJobResultMessage arm = new CollectJobResultMessage(
                        AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.myServentInfo.getIpAddress(),
                        nextJobServent.getListenerPort(),
                        nextJobServent.getIpAddress(),
                        jobName,
                        otherJobServents,
                        resultRequestingServentId,
                        jobPointFromMessage,
                        collectResultMessage.getFractalId()
                );
                MessageUtil.sendMessage(arm);
            }
        }
    }
}
