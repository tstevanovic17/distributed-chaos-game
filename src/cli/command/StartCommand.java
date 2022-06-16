package cli.command;

import app.AppConfig;
import app.JobScheduler;
import app.model.Job;
import cli.CLIParser;

public class StartCommand implements CLICommand {

    private final CLIParser parser;

    public StartCommand(CLIParser parser) {
        this.parser = parser;
    }

    @Override
    public String commandName() {
        return "start";
    }

    @Override
    public void execute(String args) {

        //imamo vise poslova nego servenata
        if(AppConfig.systemState.getServentInfoMap().size() < AppConfig.systemState.getSystemActiveJobs().size()) {
            AppConfig.timestampedErrorPrint("Not enough servents for jobs");
            return;
        }

        Job newJob = null;

        if (args == null) {
            //unos posla sa konzole
        } else {
            if(AppConfig.myServentInfo.getJobByName(args) != null) {
                newJob = AppConfig.myServentInfo.getJobByName(args);
            } else {
                //nije pronadjen posao
                AppConfig.timestampedErrorPrint("Job: " + args + " was not found.");
                return;
            }
        }

        if(AppConfig.systemState.addJob(newJob)) {
            //dodati posao u listu, pokrenuti raspodelu poslova
            JobScheduler.scheduleJob(
                    AppConfig.systemState.getServentInfoMap().size(),
                    JobScheduler.JobScheduleReason.NEW_JOB_ADDED
            );
        } else {
            //posao vec postoji u aktivnim poslovima
        }

    }

}
