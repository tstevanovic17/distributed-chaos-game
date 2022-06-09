package app;

import servent.message.NewNodeMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ServentInitializer implements Runnable {

	String lastServent = "";

	private String getLastServent() {
		int bsPort = AppConfig.BOOTSTRAP_PORT;
		String bsIpAddress = AppConfig.BOOTSTRAP_IP_ADDRESS;
		
		try {
			Socket bsSocket = new Socket(bsIpAddress, bsPort);
			
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("Hail\n" + AppConfig.myServentInfo.getIpAddress() + "\n" +
					AppConfig.myServentInfo.getListenerPort() + "\n");
			bsWriter.flush();

			Scanner bsScanner = new Scanner(bsSocket.getInputStream());
			lastServent = bsScanner.nextLine();

			bsSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return lastServent ;
	}
	
	@Override
	public void run() {

		String lastServent = getLastServent();
		
		if (lastServent.isEmpty()) {
			AppConfig.timestampedErrorPrint("Error in contacting bootstrap. Exiting...");
			System.exit(0);
		}
		if (lastServent.equals("-1")) { //bootstrap gave us -1 -> we are first
			AppConfig.myServentInfo.setId(0);
			//todo upisati nas u nesto kao cordstate
			//AppConfig.chordState.getAllNodeIdInfoMap().put(AppConfig.myServentInfo.getId(), AppConfig.myServentInfo);
			AppConfig.timestampedStandardPrint("First node in distributed chaos-game system.");
		} else { //bootstrap gave us something else - let that node tell our successor that we are here
			try {
				String[] lastServentIpAndPort = lastServent.split(":");
				int lastServentPort = Integer.parseInt(lastServentIpAndPort[1]);
				String lastServentIp = lastServentIpAndPort[0];

				NewNodeMessage nnm = new NewNodeMessage(
						AppConfig.myServentInfo.getListenerPort(),
						AppConfig.myServentInfo.getIpAddress(),
						lastServentPort,
						lastServentIp
				);

				MessageUtil.sendMessage(nnm);
			} catch (Exception e) {
				AppConfig.timestampedErrorPrint("Bad last servent data");
			}
		}

	}

}
