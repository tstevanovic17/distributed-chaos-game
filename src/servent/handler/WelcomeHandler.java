package servent.handler;

import app.AppConfig;
import app.SystemState;
import app.model.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.HashMap;
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

			System.out.println("Welcome poruku je dobio: "+AppConfig.myServentInfo.getId());

			AppConfig.systemState.initializeSystemState(AppConfig.myServentInfo);

			System.out.println("Update should be sent 1");

			UpdateMessage updateMessage = new UpdateMessage(
					AppConfig.myServentInfo.getListenerPort(),
					AppConfig.myServentInfo.getIpAddress(),
					welcomeMsg.getSenderPort(),
					welcomeMsg.getSenderIp(),
					AppConfig.systemState.getServentInfoMap(),
					new HashMap<>(),
					new ArrayList<>(),
					true
			);

			System.out.println("Update should be sent 2");

			MessageUtil.sendMessage(updateMessage);

			System.out.println("Update should be sent 3");
			
		} else {
			AppConfig.timestampedErrorPrint("Welcome handler got a message that is not WELCOME");
		}

	}

}
