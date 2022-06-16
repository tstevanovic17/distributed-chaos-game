package servent.handler;

import app.AppConfig;
import app.WorkingJobInstance;
import app.JobScheduler;
import app.model.Fractal;
import app.model.Job;
import app.model.Point;
import app.model.ServentInfo;
import servent.message.*;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JobExecutionHandler implements MessageHandler {
    private final Message clientMessage;
    private final ExecuteJobMessage executeJobMessage;
    private final Fractal fractal;
    private final List<Point> pointList;
    private final Job job;
    private final Map<Fractal, Fractal> mappedFractalJobs;
    private final JobScheduler.JobScheduleReason scheduleType;
    private final int jobSchedulerId;

    public JobExecutionHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
        executeJobMessage = (ExecuteJobMessage) clientMessage;
        fractal = executeJobMessage.getFractal();
        pointList = executeJobMessage.getStartPoints();
        job = executeJobMessage.getJob();
        mappedFractalJobs = executeJobMessage.getMappedFractalsJobs();
        scheduleType = executeJobMessage.getScheduleType();
        jobSchedulerId = executeJobMessage.getJobSchedulerId();
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() != MessageType.EXECUTE_JOB) {
            AppConfig.timestampedErrorPrint("Job execution handler got a message that is not JOB_EXECUTION");
            return;
        }

        AppConfig.systemState.setServentsJobsMap(executeJobMessage.getServentJobsMap());

        AppConfig.timestampedStandardPrint("Fractal: " + fractal.toString());
        AppConfig.timestampedStandardPrint("Job: " + job.getName());
        AppConfig.timestampedStandardPrint("Starting points: " + pointList.toString());


        //salje potvrdu čvoru koji je započeo posao
        ServentInfo schedulerServent = AppConfig.systemState.getServentById(jobSchedulerId);

        AckExecuteJobMessage ackExecuteJobMessage = new AckExecuteJobMessage(
                AppConfig.myServentInfo.getListenerPort(),
                AppConfig.myServentInfo.getIpAddress(),
                schedulerServent.getListenerPort(),
                schedulerServent.getIpAddress()
        );

        MessageUtil.sendMessage(ackExecuteJobMessage);

        if (AppConfig.systemState.getExecutionJob() != null) {
            sendMyCurrentData(mappedFractalJobs, scheduleType);
        }

        boolean newJobOrServentRemoved = scheduleType.equals(JobScheduler.JobScheduleReason.NEW_JOB_ADDED) || scheduleType.equals(JobScheduler.JobScheduleReason.SERVENT_REMOVED);

        if (newJobOrServentRemoved) {
            //racunanje koliko poruka sa podacima treba da dobijem od drugih servenata
            for (Map.Entry<Fractal, Fractal> entry : mappedFractalJobs.entrySet()) {
                if (entry.getValue().equals(fractal)) {
                    AppConfig.systemState.getTotalPointMsgExpected().getAndIncrement();
                }
            }
        } else if (mappedFractalJobs.containsKey(fractal)) {
            AppConfig.systemState.getTotalPointMsgExpected().set(1);
        }

       //cekaj dok mi drugi ne pošalju podatke
        int expectedMessagesCount = AppConfig.systemState.getTotalPointMsgExpected().get();
        AppConfig.timestampedStandardPrint("Waiting for " + expectedMessagesCount + " servents to send me their computed points");

        while (true) {
            if (AppConfig.systemState.getCalculatedPointsMsgCount().get() == expectedMessagesCount
                    || expectedMessagesCount == 0) {
                break;
            }
        }

        AppConfig.timestampedStandardPrint("Received all computed points");

        AppConfig.systemState.addJob(job);
        WorkingJobInstance workingJobInstance = new WorkingJobInstance(
                job.getName(),
                fractal.getId(),
                job.getProportion(),
                job.getWidth(),
                job.getHeight(),
                pointList
        );

        List<Point> receivedComputedPoints = new ArrayList<>(AppConfig.systemState.getReceivedComputedPoints());

        if (newJobOrServentRemoved) {
            workingJobInstance.getDrawnPoints().addAll(receivedComputedPoints);
        } else {
            List<Point> resultList = new ArrayList<>();
            for (Point myPoint : receivedComputedPoints) {

                boolean result = false;

                for (int i = 0, j = pointList.size() - 1; i < pointList.size(); j = i++) {

                    if ((pointList.get(i).getCoordY() > myPoint.getCoordY()) != (pointList.get(j).getCoordY() > myPoint.getCoordY()) &&
                            (myPoint.getCoordX() < (pointList.get(j).getCoordX() - pointList.get(i).getCoordX()) *
                                    (myPoint.getCoordY() - pointList.get(i).getCoordY()) /
                                    (pointList.get(j).getCoordY() - pointList.get(i).getCoordY()) + pointList.get(i).getCoordX())) {
                        result = !result;
                    }
                }

                if (result) {
                    resultList.add(myPoint);
                }
            }
            workingJobInstance.getDrawnPoints().addAll(resultList);
        }

        AppConfig.systemState.setExecutionJob(workingJobInstance);
        Thread jobExecutionThread = new Thread(workingJobInstance);
        jobExecutionThread.start();

        //resetovanje primljenih podataka za sledeci put
        AppConfig.systemState.resetAfterReceivedComputedPoints();
    }

    static void sendMyCurrentData(Map<Fractal, Fractal> mappedFractalJobs, JobScheduler.JobScheduleReason scheduleType) {
        WorkingJobInstance je = AppConfig.systemState.getExecutionJob();
        List<Point> myComputedPoints = new ArrayList<>(je.getDrawnPoints());
        Fractal myOldFractalJob = new Fractal(je.getFractalId(), je.getJobName());

        if (mappedFractalJobs.containsKey(myOldFractalJob) &&
                (scheduleType.equals(JobScheduler.JobScheduleReason.NEW_JOB_ADDED) ||
                        scheduleType.equals(JobScheduler.JobScheduleReason.SERVENT_REMOVED))) {

            sendComputedPointsMessage(myComputedPoints, myOldFractalJob, mappedFractalJobs.get(myOldFractalJob));
        } else {
            for (Map.Entry<Fractal, Fractal> entry : mappedFractalJobs.entrySet()) {
                if (entry.getValue().equals(myOldFractalJob)) {
                    sendComputedPointsMessage(myComputedPoints, myOldFractalJob, entry.getKey());
                }
            }
        }

        je.stop();
    }

    private static void sendComputedPointsMessage(
            List<Point> computedPoints,
            Fractal myOldFractalJob,
            Fractal receiverFractalJob
    ) {
        int receiverId = AppConfig.systemState.getServentIdForFractal(
                new Fractal(receiverFractalJob.getId(), receiverFractalJob.getJob())
        );
        ServentInfo intercessor = AppConfig.systemState.getServentById(receiverId);

        ComputedPointsMessage cpm = new ComputedPointsMessage(
                AppConfig.myServentInfo.getListenerPort(),
                AppConfig.myServentInfo.getIpAddress(),
                intercessor.getListenerPort(),
                intercessor.getIpAddress(),
                myOldFractalJob.getJob(),
                myOldFractalJob.getId(),
                computedPoints
        );

        MessageUtil.sendMessage(cpm);
    }
}
