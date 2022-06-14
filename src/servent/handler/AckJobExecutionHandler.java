package servent.handler;

import app.AppConfig;
import app.model.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;

public class AckJobExecutionHandler implements MessageHandler {

    private final Message clientMessage;

    public AckJobExecutionHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.ACK_EXECUTE_JOB) {
            AppConfig.timestampedErrorPrint("Ack job execution got a message that is not ACK_JOB_EXECUTION");
            return;
        }

        int senderId = -1;

        for (ServentInfo i : AppConfig.systemState.getServentInfoMap().values()) {
            if (i.getListenerPort() == clientMessage.getSenderPort() && i.getIpAddress().equals(clientMessage.getSenderIp())) {
                senderId = i.getId();
            }
        }

        AppConfig.timestampedStandardPrint("Acknowledge message received from: " + senderId);

        AppConfig.systemState.getAckMsgCount().getAndIncrement();
    }

}
