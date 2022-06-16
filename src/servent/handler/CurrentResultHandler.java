package servent.handler;

import app.AppConfig;
import app.model.Point;
import servent.message.CurrentResultMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.List;

public class CurrentResultHandler implements MessageHandler {

    private final Message clientMessage;

    public CurrentResultHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.CURRENT_RESULT) {
            AppConfig.timestampedErrorPrint("Computed points handler got a message that is not COMPUTED_POINTS");
            return;
        }

        CurrentResultMessage currentResultMessage = (CurrentResultMessage) clientMessage;
        String jobName = currentResultMessage.getJobName();
        String fractalId = currentResultMessage.getFractalId();
        List<Point> computedPoints = currentResultMessage.getComputedPoints();

        AppConfig.timestampedStandardPrint("Received computed points from {fractalID=" + fractalId + ", jobName=" + jobName + "}");
        AppConfig.systemState.addComputedPoints(computedPoints);
    }
}
