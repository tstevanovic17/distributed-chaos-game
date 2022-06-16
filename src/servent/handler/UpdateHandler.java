package servent.handler;

import app.AppConfig;
import app.JobScheduler;
import servent.message.RescheduleJobMessage;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.UpdateMessage;
import servent.message.util.MessageUtil;

public class UpdateHandler implements MessageHandler {

    private final UpdateMessage clientMessage;

    public UpdateHandler(Message clientMessage) {
        this.clientMessage = (UpdateMessage) clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.UPDATE) {

            UpdateMessage message = (UpdateMessage) clientMessage;

            /*
            scenario
            novi cvor salje poruku cvoru koji ga je ukljucio
            cvor koji ga je ukljucio, svima salje poruku sa
            svim podacima koje on ima + novim cvorom
            niko ne odgovara na ovu poruku
             */


            if (clientMessage.shouldReply()) {
                //cvor koji je ukljucio novog cvora je dobio poruku

                //dopunjavamo listu cvorova za koje znamo i saljemo svim cvorovima update
                AppConfig.systemState.setServentInfoMap(message.getServentInfoMap());

                UpdateMessage reply = new UpdateMessage(
                        clientMessage.getReceiverPort(),
                        clientMessage.getReceiverIp(),
                        clientMessage.getSenderPort(),
                        clientMessage.getSenderIp(),
                        AppConfig.systemState.getServentInfoMap(),
                        AppConfig.systemState.getServentsJobsMap(),
                        AppConfig.systemState.getSystemActiveJobs(),
                        false
                );

                MessageUtil.sendMessage(reply);

            } else {
                //ostali cvorovi ukljucujuci i novi cvor su dobili poruku sa svim inormacijama iz sistema
                AppConfig.systemState.setSystemActiveJobs(message.getJobs());
                AppConfig.systemState.setServentInfoMap(message.getServentInfoMap());
                AppConfig.systemState.setServentsJobsMap(message.getServentsJobsMap());

                /*

                if (message.getJobs().size() > 0) {
                    AppConfig.timestampedStandardPrint("Rescheduling jobs");

                    RescheduleJobMessage jsm = new RescheduleJobMessage(
                            AppConfig.myServentInfo.getListenerPort(),
                            AppConfig.myServentInfo.getIpAddress(),
                            AppConfig.myServentInfo.getListenerPort(),
                            AppConfig.myServentInfo.getIpAddress(),
                            JobScheduler.JobScheduleReason.JOB_REMOVED
                    );

                    MessageUtil.sendMessage(jsm);
                }


                 */
            }

        } else {
            AppConfig.timestampedErrorPrint("Update message handler got message that is not UPDATE");
        }
    }

}
