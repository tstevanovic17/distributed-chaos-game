package servent.handler;

import app.AppConfig;
import app.model.Point;
import servent.message.ComputedPointsMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.util.List;

public class ComputedPointsHandler implements MessageHandler {

    private final Message clientMessage;

    public ComputedPointsHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.COMPUTED_POINTS) {
            AppConfig.timestampedErrorPrint("Computed points handler got a message that is not COMPUTED_POINTS");
            return;
        }

        ComputedPointsMessage computedPointsMessage = (ComputedPointsMessage) clientMessage;
        String jobName = computedPointsMessage.getJobName();
        String fractalId = computedPointsMessage.getFractalId();
        List<Point> computedPoints = computedPointsMessage.getComputedPoints();

        AppConfig.timestampedStandardPrint("Received computed points from {fractalID=" + fractalId + ", jobName=" + jobName + "}");
        AppConfig.systemState.addComputedPoints(computedPoints);
    }
}
