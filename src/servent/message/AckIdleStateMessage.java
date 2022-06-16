package servent.message;

public class AckIdleStateMessage extends BasicMessage {

    private static final long serialVersionUID = -1236109238293298393L;

    public AckIdleStateMessage(
            int senderPort,
            String senderIpAddress,
            int receiverPort,
            String receiverIpAddress
    ) {
        super(MessageType.ACK_IDLE_STATE, senderPort, senderIpAddress, receiverPort,  receiverIpAddress, "");
    }

}
