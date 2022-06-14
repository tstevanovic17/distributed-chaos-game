package servent.message;

public class AckExecuteJobMessage extends BasicMessage {

    private static final long serialVersionUID = 1242342344212385445L;

    public AckExecuteJobMessage(
            int senderPort,
            String senderIpAddress,
            int receiverPort,
            String receiverIpAddress
    ) {
        super(MessageType.ACK_EXECUTE_JOB, senderPort, senderIpAddress, receiverPort, receiverIpAddress, "");
    }

}
