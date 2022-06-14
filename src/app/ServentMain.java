package app;

import app.model.Job;
import cli.CLIParser;
import servent.SimpleServentListener;

import java.util.List;

/**
 * Describes the procedure for starting a single Servent
 *
 * @author bmilojkovic
 */
public class ServentMain {

	/**
	 * Command line arguments are:
	 * 0 - path to servent list file
	 * 1 - this servent's id
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			AppConfig.timestampedErrorPrint("Please provide servent list file and id of this servent.");
		}
		
		int serventId = -1;
		int portNumber = -1;
		
		String serventListFile = args[0];

		try {
			serventId = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			AppConfig.timestampedErrorPrint("Second argument should be an int. Exiting...");
			System.exit(0);
		}

		AppConfig.readServentConfig(serventListFile);

		try {
			portNumber = AppConfig.myServentInfo.getListenerPort();
			
			if (portNumber < 1000 || portNumber > 2000) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Port number should be in range 1000-2000. Exiting...");
			System.exit(0);
		}
		
		AppConfig.timestampedStandardPrint("Starting servent " + AppConfig.myServentInfo);

		List<Job> serventJobs = AppConfig.myServentInfo.getJobs();

		if (!serventJobs.isEmpty()) {
			AppConfig.timestampedStandardPrint("Listing servent jobs:");
			for (Job serventJob : serventJobs) {
				AppConfig.timestampedStandardPrint("Job name: " + serventJob.getName());
			}
		}

		SimpleServentListener simpleListener = new SimpleServentListener();
		Thread listenerThread = new Thread(simpleListener);
		listenerThread.start();
		
		CLIParser cliParser = new CLIParser(simpleListener);
		Thread cliThread = new Thread(cliParser);
		cliThread.start();

		ServentInitializer serventInitializer = new ServentInitializer();
		Thread initializerThread = new Thread(serventInitializer);
		initializerThread.start();

	}
}
