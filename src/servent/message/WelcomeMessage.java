package servent.message;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;
	private final int newServentId;

	public WelcomeMessage(
		int senderPort,
		String senderIp,
		int receiverPort,
		String receiverIp,
		int newServentId
	) {
		super(MessageType.WELCOME, senderPort, senderIp, receiverPort, receiverIp, "Welcome!");
		
		this.newServentId = newServentId;
	}

	public int getNewServentId() {
		return newServentId;
	}

}
