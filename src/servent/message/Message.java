package servent.message;

import java.io.Serializable;
import java.util.List;

import app.model.ServentInfo;

/**
 * This is your basic message. It should cover most needs.
 * It supports the following stuff:
 * <ul>
 * 	<li>Basic attributes:<ul>
 * 		<li>Message ID - unique on a single servent.</li>
 * 		<li>Message type</li>
 * 		<li>Info about the initial message sender</li>
 * 		<li>Receiver info</li>
 * 		<li>Route list (constructed via <code>makeMeASender</code> )</li>
 * 		<li>Arbitrary message text</li>
 * 		</ul>
 * 	<li>Is serializable</li>
 * 	<li>Is immutable</li>
 * 	<li>Modification methods:<ul>
 * 		<li>makeMeASender - adds the current servent to the route list</li>
 * 		<li>changeReceiver - changes the receiver info attribute</li>
 * 		<li>IMPORTANT: if your subclass adds an attribute that you need copied,
 * 		and you want to use these methods, make sure to override them to include your attribute.</li>
 * 		</ul>
 * 	<li>Equality and hashability based on message id and original sender id</li>
 * </ul>
 * @author bmilojkovic
 *
 */
public interface Message extends Serializable {

	MessageType getMessageType();

	String getMessageText();

	int getMessageId();

	int getSenderPort();

	String getSenderIp();

	int getReceiverPort();

	String getReceiverIp();
	
}
