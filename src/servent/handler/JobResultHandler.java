package servent.handler;

import app.AppConfig;
import app.SaveResultsImage;
import app.model.Point;
import servent.message.JobResultMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.List;

public class JobResultHandler implements MessageHandler {

    private Message clientMessage;

    public JobResultHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.JOB_RESULT) {
            AppConfig.timestampedErrorPrint("Job result handler got a message that is not JOB_RESULT");
            return;
        }

        JobResultMessage jobResultMessage = (JobResultMessage) clientMessage;
        List<Point> resultPoints = jobResultMessage.getComputedPoints();
        String jobName = jobResultMessage.getJobName();
        int width = jobResultMessage.getWidth();
        int height = jobResultMessage.getHeight();
        double proportion = jobResultMessage.getProportion();


        SaveResultsImage.saveResultsImage(jobName, jobResultMessage.getFractalId(), width, height, proportion, resultPoints);
    }
}
