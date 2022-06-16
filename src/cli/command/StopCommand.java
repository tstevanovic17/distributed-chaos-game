package cli.command;

import app.AppConfig;
import app.JobScheduler;
import app.model.Job;
import app.model.ServentInfo;
import servent.message.RescheduleJobMessage;
import servent.message.StopJobMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class StopCommand implements CLICommand {

	@Override
	public String commandName() {
		return "stop";
	}

	@Override
	public void execute(String args) {

		Job job = null;

		for (Job i : AppConfig.systemState.getSystemActiveJobs()) {
			if (i.getName().equals(args)) {
				job = i;
				break;
			}
		}

		if (job != null) {

			for(Map.Entry<Integer, ServentInfo> i : AppConfig.systemState.getServentInfoMap().entrySet()) {

				ServentInfo currServent = AppConfig.systemState.getServentById(i.getKey());

				StopJobMessage message = new StopJobMessage(
						AppConfig.myServentInfo.getListenerPort(),
						AppConfig.myServentInfo.getIpAddress(),
						currServent.getListenerPort(),
						currServent.getIpAddress(),
						job.getName()
				);
				MessageUtil.sendMessage(message);

			}

			if (AppConfig.systemState.getSystemActiveJobs().size() > 0) {

				AppConfig.timestampedStandardPrint("Rescheduling jobs");

				RescheduleJobMessage jsm = new RescheduleJobMessage(
						AppConfig.myServentInfo.getListenerPort(),
						AppConfig.myServentInfo.getIpAddress(),
						AppConfig.myServentInfo.getListenerPort(),
						AppConfig.myServentInfo.getIpAddress(),
						JobScheduler.JobScheduleReason.JOB_REMOVED
				);

				//MessageUtil.sendMessage(jsm);
			}


		} else {
			AppConfig.timestampedErrorPrint("Job not found.");
		}

	}

}
