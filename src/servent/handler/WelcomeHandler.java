package servent.handler;

import app.AppConfig;
import app.SystemState;
import app.model.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

import java.util.List;

public class WelcomeHandler implements MessageHandler {

	private final Message clientMessage;
	
	public WelcomeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.WELCOME) {

			WelcomeMessage welcomeMsg = (WelcomeMessage)clientMessage;

			AppConfig.myServentInfo.setId(welcomeMsg.getNewServentId());

			AppConfig.systemState = new SystemState(AppConfig.myServentInfo);

			UpdateMessage updateMessage = new UpdateMessage(
					AppConfig.myServentInfo.getListenerPort(),
					AppConfig.myServentInfo.getIpAddress(),
					welcomeMsg.getSenderPort(),
					welcomeMsg.getSenderIp(),
					(List<ServentInfo>) AppConfig.systemState.getServentInfoMap().values(),
					null,
					null,
					true
			);
			MessageUtil.sendMessage(updateMessage);
			
		} else {
			AppConfig.timestampedErrorPrint("Welcome handler got a message that is not WELCOME");
		}

	}

}
