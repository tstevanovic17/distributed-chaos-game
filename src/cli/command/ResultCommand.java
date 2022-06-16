package cli.command;

import app.AppConfig;
import app.model.Fractal;
import app.model.ServentInfo;
import servent.message.CollectJobResultMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;

public class ResultCommand implements CLICommand {

    @Override
    public String commandName() {
        return "result";
    }

    @Override
    public void execute(String args) {
        String jobName = args.split(" ")[0];

        String fractalId = null;
        if (args.contains(" ")) {
            fractalId = args.split(" ")[1];
        }

        // get result for whole job
        // imamo dva slucaja, samo jedan cvor ima sve rezultate ili imamo onoliko cvorova koliko je tacaka posla
        if (fractalId == null) {
            AppConfig.timestampedStandardPrint("Collecting computed points for job \"" + jobName + "\"...");


            //ove informacije nam ne trebaju, treba nam samo jedan servent
            //ili svi serventi u zavrisnosti kako je podijeljen posao
            /*
            int firstServentId = AppConfig.systemState.getFirstIdForJob(jobName);
            int lastServentId = AppConfig.systemState.getLastIdForJob(jobName);
             */

            //poslati ovu listu cvorova, bez cvora kojem saljemo prvu poruku
            //u hendleru ako je lista prazna, saljemo rezultat
            //ako nije, brisemo jedan cvor i saljemo poruku kako i do sad radi sistem
            List<Integer> employedServentsIds = AppConfig.systemState.getAllServentIdsForJob(jobName);

            if (employedServentsIds.isEmpty()) {
                AppConfig.timestampedErrorPrint("Something went wrong... no servents work on this job");
            } else {
                Integer oneServentId = employedServentsIds.remove(0);
                ServentInfo oneServentInfo = AppConfig.systemState.getServentById(oneServentId);

                CollectJobResultMessage askForJobResultMsg = new CollectJobResultMessage(
                        AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.myServentInfo.getIpAddress(),
                        oneServentInfo.getListenerPort(),
                        oneServentInfo.getIpAddress(),
                        jobName,
                        employedServentsIds,
                        AppConfig.myServentInfo.getId(),
                        new ArrayList<>(),
                        null
                );

                MessageUtil.sendMessage(askForJobResultMsg);
            }

            //ako mozemo isto u ovom kodu sacekati sve rezultate i sumirati ih, kao sto radimo za ack poruke
            //moze li odje biti problema sa asynhronim stizanjem poruka?

        }

        // get result for specific job and fractalId
        else {
            //nadjemo serventa kojem pripada fraktal
            //trazimo mu rezultat
            //iskoristicemo isti tip poruke iako kod nevene imamo drugi tip poruke

            int serventId = AppConfig.systemState.getServentIdForFractal(new Fractal(fractalId, jobName));
            ServentInfo fractalOwnerServent = AppConfig.systemState.getServentById(serventId);

            CollectJobResultMessage fractalResultMessage = new CollectJobResultMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.myServentInfo.getIpAddress(),
                    fractalOwnerServent.getListenerPort(),
                    fractalOwnerServent.getIpAddress(),
                    jobName,
                    new ArrayList<>(),
                    AppConfig.myServentInfo.getId(),
                    new ArrayList<>(),
                    fractalId
            );

            MessageUtil.sendMessage(fractalResultMessage);
        }

    }
}
