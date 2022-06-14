package servent.message;

public class AckIdleMessage extends BasicMessage {

    private static final long serialVersionUID = -1236109238293298393L;

    public AckIdleMessage(
            int senderPort,
            String senderIpAddress,
            int receiverPort,
            String receiverIpAddress
    ) {
        super(MessageType.ACK_IDLE_STATE, senderPort, senderIpAddress, receiverPort,  receiverIpAddress, "");
    }

}
