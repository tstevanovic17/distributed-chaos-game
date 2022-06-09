package servent.message;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A default message implementation. This should cover most situations.
 * If you want to add stuff, remember to think about the modificator methods.
 * If you don't override the modificators, you might drop stuff.
 * @author bmilojkovic
 *
 */
public class BasicMessage implements Message {

	private static final long serialVersionUID = -9075856313609777945L;
	private final MessageType type;
	private final int senderPort;
	private final int receiverPort;
	private final String senderIp;
	private final String receiverIp;
	private final String messageText;
	
	//This gives us a unique id - incremented in every natural constructor.
	private static final AtomicInteger messageCounter = new AtomicInteger(0);
	private final int messageId;

	@Override
	public MessageType getMessageType() {
		return type;
	}

	@Override
	public String getMessageText() {
		return messageText;
	}
	
	@Override
	public int getMessageId() {
		return messageId;
	}

	public int getSenderPort() {
		return senderPort;
	}

	public int getReceiverPort() {
		return receiverPort;
	}

	public MessageType getType() {
		return type;
	}

	public String getSenderIp() {
		return senderIp;
	}

	public String getReceiverIp() {
		return receiverIp;
	}

	protected BasicMessage(
			MessageType type,
			int senderPort,
			String senderIp,
			int receiverPort,
			String receiverIp,
			String messageText
	) {
		this.type = type;
		this.receiverPort = receiverPort;
		this.senderPort = senderPort;
		this.senderIp = senderIp;
		this.receiverIp = receiverIp;
		this.messageText = messageText;
		
		this.messageId = messageCounter.getAndIncrement();
	}

	/**
	 * Comparing messages is based on their unique id and the original sender id.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BasicMessage) {
			BasicMessage other = (BasicMessage)obj;

			return getMessageId() == other.getMessageId() &&
					getSenderPort() == other.getSenderPort() &&
					getSenderIp().equals(other.getSenderIp());
		}
		
		return false;
	}
	
	/**
	 * Hash needs to mirror equals, especially if we are gonna keep this object
	 * in a set or a map. So, this is based on message id and original sender id also.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getMessageId(), getSenderPort());
	}
	
	/**
	 * Returns the message in the format: <code>[sender_id|message_id|text|type|receiver_id]</code>
	 */
	@Override
	public String toString() {
		return "BasicMessage{" +
				"type=" + type +
				", senderPort=" + senderPort +
				", receiverPort=" + receiverPort +
				", senderIp='" + senderIp + '\'' +
				", receiverIp='" + receiverIp + '\'' +
				", messageText='" + messageText + '\'' +
				", messageId=" + messageId +
				'}';
	}

}
