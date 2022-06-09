package servent.message;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import app.model.ServentInfo;

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
	private final ServentInfo originalSenderInfo;
	private final ServentInfo receiverInfo;
	private final String messageText;
	
	//This gives us a unique id - incremented in every natural constructor.
	private static AtomicInteger messageCounter = new AtomicInteger(0);
	private final int messageId;
	
	public BasicMessage(MessageType type, int receiverPort, int senderPort, ServentInfo originalSenderInfo, ServentInfo receiverInfo) {
		this.type = type;
		this.receiverPort = receiverPort;
		this.senderPort = senderPort;
		this.originalSenderInfo = originalSenderInfo;
		this.receiverInfo = receiverInfo;
		this.messageText = "";
		
		this.messageId = messageCounter.getAndIncrement();
	}
	
	public BasicMessage(MessageType type, int receiverPort, int senderPort, ServentInfo originalSenderInfo, ServentInfo receiverInfo,
						String messageText) {
		this.type = type;
		this.receiverPort = receiverPort;
		this.senderPort = senderPort;
		this.originalSenderInfo = originalSenderInfo;
		this.receiverInfo = receiverInfo;
		this.messageText = messageText;
		
		this.messageId = messageCounter.getAndIncrement();
	}
	
	@Override
	public MessageType getMessageType() {
		return type;
	}

	@Override
	public ServentInfo getOriginalSenderInfo() {
		return originalSenderInfo;
	}

	@Override
	public ServentInfo getReceiverInfo() {
		return receiverInfo;
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

	protected BasicMessage(MessageType type, int receiverPort, int senderPort, ServentInfo originalSenderInfo, ServentInfo receiverInfo,
						   String messageText, int messageId) {
		this.type = type;
		this.receiverPort = receiverPort;
		this.senderPort = senderPort;
		this.originalSenderInfo = originalSenderInfo;
		this.receiverInfo = receiverInfo;
		this.messageText = messageText;
		
		this.messageId = messageId;
	}

	/**
	 * Comparing messages is based on their unique id and the original sender id.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BasicMessage) {
			BasicMessage other = (BasicMessage)obj;

			return getMessageId() == other.getMessageId() &&
					getOriginalSenderInfo().getId() == other.getOriginalSenderInfo().getId();
		}
		
		return false;
	}
	
	/**
	 * Hash needs to mirror equals, especially if we are gonna keep this object
	 * in a set or a map. So, this is based on message id and original sender id also.
	 */
	@Override
	public int hashCode() {
		return Objects.hash(getMessageId(), getOriginalSenderInfo().getId());
	}
	
	/**
	 * Returns the message in the format: <code>[sender_id|message_id|text|type|receiver_id]</code>
	 */
	@Override
	public String toString() {
		return "[" + getOriginalSenderInfo().getId() + "|" + getMessageId() + "|" +
					getMessageText() + "|" + getMessageType() + "|" +
					getReceiverInfo().getId() + "]";
	}

	/**
	 * Empty implementation, which will be suitable for most messages.
	 */
	@Override
	public void sendEffect() {
		
	}
}
