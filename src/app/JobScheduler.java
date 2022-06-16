package app;

import app.model.Fractal;
import app.model.Job;
import app.model.Point;
import app.model.ServentInfo;
import servent.message.ExecuteJobMessage;
import servent.message.IdleStateMessage;
import servent.message.util.MessageUtil;

import java.util.*;

public class JobScheduler {

    //raspodjeljuje koliko ce kojem poslu da da cvorova
    private static Map<Job, Integer> assignNumberOfServentsToJob(List<Job> jobs) {
        int numberOfServents = AppConfig.systemState.getServentInfoMap().size();

        System.out.println("assignNumberOfServentsToJob: "+numberOfServents);

        Map<Job, Integer> numberOfServantsPerJob = new HashMap<>();

        int serventsPerJob = numberOfServents / jobs.size();
        int remainingServents = numberOfServents % jobs.size();

        for (Job job : jobs) {
            if (remainingServents > 0) {
                numberOfServantsPerJob.put(job, serventsPerJob + 1);
                remainingServents--;
            } else {
                numberOfServantsPerJob.put(job, serventsPerJob);
            }
        }

        return numberOfServantsPerJob;
    }

    //kako najveci broj cvorova oprijedeljenih za posao da zaposli
    //za pocetak razmatramo najvise dubinu 1
    private static int distributeServantsForJob(int numberOfServents, int numberOfPoints) {

        if (numberOfPoints > numberOfServents) {
            return 1;
        } else {
            return numberOfPoints;
        }

    }


    //daje fraktalne ideve
    private static List<String> generateFractalIdForJob(int numberOfServents) {

        List<String> fractals = new ArrayList<>();

        for (int i = 0; i<numberOfServents; i++) {
            fractals.add(String.valueOf(i));
        }

        return fractals;

    }


    //dijeljenje regiona
    public static void calculateRegionsAndSendMessages(
            List<Point> points,
            double factor,
            List<String> fractals,
            Job job,
            Map<Integer, Fractal> serventJobsMap,
            Map<Fractal, Fractal> mappedFractals,
            JobScheduleReason scheduleReason
    ) {

        for (int i = 0; i<points.size(); i++) {
            List<Point> regionPoints = new ArrayList<>();

            Point fractalPoint = points.get(i);

            for (int j = 0; j < points.size(); j++) {
                if (i == j) {
                    regionPoints.add(fractalPoint);
                } else {
                    Point currentPoint = points.get(j);
                    int newX = (int) (fractalPoint.getCoordX() + factor * (currentPoint.getCoordX() - fractalPoint.getCoordX()));
                    int newY = (int) (fractalPoint.getCoordY() + factor * (currentPoint.getCoordY() - fractalPoint.getCoordY()));
                    Point newPoint = new Point(newX, newY);
                    regionPoints.add(newPoint);
                }
            }

            //smijemo li ovako fraktale
            //hocemo sivma da saljemo direkt poruku
            int serventId = AppConfig.systemState.getServentIdForFractal(new Fractal(fractals.get(i), job.getName()));
            ServentInfo receiverServent = AppConfig.systemState.getServentById(serventId);

            System.out.println("-------------- Region points for: "+i+ "----------------");
            System.out.println(regionPoints.get(0) + " ----- " + regionPoints.get(1) + " ----- " + regionPoints.get(2));


            Fractal fractal = new Fractal(fractals.get(i), job.getName());

            ExecuteJobMessage jobExecutionMessage = new ExecuteJobMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    AppConfig.myServentInfo.getIpAddress(),
                    receiverServent.getListenerPort(),
                    receiverServent.getIpAddress(),
                    fractal,
                    regionPoints,
                    job,
                    serventJobsMap,
                    mappedFractals,
                    scheduleReason,
                    AppConfig.myServentInfo.getId()
            );

            MessageUtil.sendMessage(jobExecutionMessage);

        }

    }

    //izmijeniti malo
    private static Map<Fractal, Fractal> scheduleMap(
            List<String> oldFractals,
            List<String> newFractals,
            String jobName,
            JobScheduleReason scheduleReason
    ) {
        Map<Fractal, Fractal> result = new HashMap<>();

        switch (scheduleReason) {
            //
            case NEW_JOB_ADDED:
            case SERVENT_REMOVED:

                for (String oldOne: oldFractals) {
                    if (newFractals.size() == 1) {   // only one servent is executing the job, map to all old ones
                        result.put(new Fractal(oldOne, jobName), new Fractal(newFractals.get(0), jobName));
                        continue;
                    }

                    for (String newOne: newFractals) {
                        if (oldOne.startsWith(newOne)) {
                            result.put(new Fractal(oldOne, jobName), new Fractal(newOne, jobName));
                        }
                    }
                }
                return result;

            case JOB_REMOVED:
            case NEW_SERVENT_ADDED:
                for (String newOne: newFractals) {
                    if (oldFractals.size() == 1) {
                        result.put(new Fractal(newOne, jobName), new Fractal(oldFractals.get(0), jobName));
                        continue;
                    }

                    for (String oldOne: oldFractals) {
                        if (newOne.startsWith(oldOne)) {
                            result.put(new Fractal(newOne, jobName), new Fractal(oldOne, jobName));
                        }
                    }
                }
                return result;
        }

        return result;
    }

    //todo promijeniti malo
    private static List<String> jobFractals(Map<Integer, Fractal> serventJobs, String jobName) {
        List<String> list = new ArrayList<>();
        for (Map.Entry<Integer, Fractal> entry: serventJobs.entrySet()) {
            if (entry.getValue().getJob().equals(jobName)) {
                list.add(entry.getValue().getId());
            }
        }
        Collections.sort(list);
        return list;
    }

    public static void scheduleJob(int serventCount, JobScheduleReason reason) {

        AppConfig.timestampedStandardPrint("Job scheduling because of: " + reason);

        List<Job> activeJobs = AppConfig.systemState.getSystemActiveJobs();
        Map<Job, Integer> jobServentCount = assignNumberOfServentsToJob(activeJobs);

        AppConfig.timestampedStandardPrint("Servent count for jobs:");
        AppConfig.timestampedStandardPrint(String.valueOf(jobServentCount));

        List<Fractal> fractalList = new ArrayList<>();
        Map<Job, List<String>> jobFractalsMap = new HashMap<>();
        for (Map.Entry<Job, Integer> entry : jobServentCount.entrySet()) {

            // compute number of servents needed to execute the job
            int distributedServentsNumber = distributeServantsForJob(
                    entry.getValue(),
                    entry.getKey().getPointsCount()
            );

            AppConfig.timestampedStandardPrint("Number of employed servents for job: " + entry.getKey().getName() + " : " + entry.getValue());

            // compute fractal ids for current job
            List<String> fractalsForJob = generateFractalIdForJob(distributedServentsNumber);
            AppConfig.timestampedStandardPrint("Fractals for job: " + entry.getKey().getName() + " : " + fractalsForJob);
            jobFractalsMap.put(entry.getKey(), fractalsForJob);

            // add fractalIds and jobs
            for (String fractalId : fractalsForJob) {
                fractalList.add(new Fractal(fractalId, entry.getKey().getName()));
            }
        }

        System.out.println("Frctal list: " + fractalList);

        //razdaje fractalId-eve serventima redom, dok svaki fraktal id ne dodijeli nekom serventu.
        // map id -> fractal -> job

        Map<Integer, Fractal> previousJobs = new HashMap<>(AppConfig.systemState.getServentsJobsMap());

        Map<Integer, Fractal> newJobs = new HashMap<>();
        for (Map.Entry<Integer, ServentInfo> entry : AppConfig.systemState.getServentInfoMap().entrySet()) {
            newJobs.put(entry.getKey(), fractalList.remove(0));
            if (fractalList.isEmpty()) {
                break;
            }
        }
        AppConfig.systemState.setServentsJobsMap(newJobs);

        AppConfig.timestampedStandardPrint("New jobs:");
        AppConfig.timestampedStandardPrint(newJobs.toString());

        // map all old fractals to new ones
        //why do we do this?
        //to transver allready calculated values?
        //if jobs were allready running

        AppConfig.timestampedStandardPrint("Previous jobs:");

        AppConfig.timestampedStandardPrint(previousJobs.toString());

        Map<Fractal, Fractal> mappedFractals = new HashMap<>();
        for (Map.Entry<Job, List<String>> entry: jobFractalsMap.entrySet()) {
            String currentJobName = entry.getKey().getName();
            List<String> oldFractals = jobFractals(previousJobs, currentJobName);

            Map<Fractal, Fractal> currentMapped = scheduleMap(
                    oldFractals,
                    entry.getValue(),
                    currentJobName,
                    reason
            );

            mappedFractals.putAll(currentMapped);
        }

        AppConfig.timestampedStandardPrint("Job fractals map: \n" + jobFractalsMap);

        for (Map.Entry<Job, List<String>> entry: jobFractalsMap.entrySet()) {
            Job currentJob = entry.getKey();
            List<String> fractals = entry.getValue();

            // compute initial job division and send messages
            List<Point> jobPoints = currentJob.getPoints();
            double factor = currentJob.getProportion();

            if (fractals.size() == 1) { // only one node is executing the job
                int serventId = AppConfig.systemState.getServentIdForFractal(new Fractal(fractals.get(0), currentJob.getName()));
                ServentInfo receiverServent = AppConfig.systemState.getServentById(serventId);

                Fractal fractal = new Fractal(fractals.get(0), currentJob.getName());

                ExecuteJobMessage message = new ExecuteJobMessage(
                        AppConfig.myServentInfo.getListenerPort(),
                        AppConfig.myServentInfo.getIpAddress(),
                        receiverServent.getListenerPort(),
                        receiverServent.getIpAddress(),
                        fractal,
                        jobPoints,
                        currentJob,
                        newJobs,
                        mappedFractals,
                        reason,
                        AppConfig.myServentInfo.getId()
                );
                MessageUtil.sendMessage(message);
            } else {
                calculateRegionsAndSendMessages(
                        jobPoints,
                        factor,
                        fractals,
                        currentJob,
                        newJobs,
                        mappedFractals,
                        reason
                );
            }
        }
        notifyIdleServents(mappedFractals, reason);
        collectAckMessages(serventCount);

    }

    private static void notifyIdleServents(Map<Fractal, Fractal> mappedFractals, JobScheduleReason scheduleReason) {
        // send to idle nodes that they are idle and new job division
        for (Map.Entry<Integer, ServentInfo> entry: AppConfig.systemState.getServentInfoMap().entrySet()) {
            int serventId = entry.getKey();

            if (AppConfig.systemState.getServentsJobsMap().containsKey(serventId)) {
                continue;
            }

            ServentInfo nextServent = AppConfig.systemState.getServentById(serventId);

            IdleStateMessage idleMessage = new IdleStateMessage(
                    AppConfig.myServentInfo.getListenerPort(),
                    nextServent.getListenerPort(),
                    AppConfig.myServentInfo.getIpAddress(),
                    nextServent.getIpAddress(),
                    new HashMap<>(AppConfig.systemState.getServentsJobsMap()),
                    mappedFractals,
                    new ArrayList<>(AppConfig.systemState.getSystemActiveJobs()),
                    scheduleReason,
                    AppConfig.myServentInfo.getId()
            );

            MessageUtil.sendMessage(idleMessage);
        }
    }

    private static void collectAckMessages(int serventCount) {

        // wait for others to send ack job execution or ack idle messages
        AppConfig.timestampedStandardPrint("collecting ack from servents...");
        while (true) {
            if (AppConfig.systemState.getAckMsgCount().get() == serventCount) {
                break;
            }
        }
        AppConfig.timestampedStandardPrint("all servents send ack...");

        AppConfig.systemState.getAckMsgCount().set(0);
    }

    public enum JobScheduleReason {
        NEW_JOB_ADDED,
        JOB_REMOVED,
        NEW_SERVENT_ADDED,
        SERVENT_REMOVED
    }
}
