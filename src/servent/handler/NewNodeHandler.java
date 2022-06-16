package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class NewNodeHandler implements MessageHandler {

	private final Message clientMessage;
	
	public NewNodeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.NEW_NODE) {
			int newServentPort = clientMessage.getSenderPort();
			String newServentIp = clientMessage.getSenderIp();

			int ourPort = clientMessage.getReceiverPort();
			String ourIp = clientMessage.getReceiverIp();

			int ourId = AppConfig.myServentInfo.getId();

			WelcomeMessage message = new WelcomeMessage(
					ourPort,
					ourIp,
					newServentPort,
					newServentIp,
					ourId + 1
			);

			MessageUtil.sendMessage(message);
		} else {
			AppConfig.timestampedErrorPrint("New node handler got something that is not new node message.");
		}

	}

}
