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

    //respodeljuje koliko ce kojem poslu da da cvorova
    private static Map<Job, Integer> assignNumberOfServentsToJob(List<Job> jobs) {
        int numberOfServents = AppConfig.systemState.getServentInfoMap().size();

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

    //kako najveci broj cvorova opredeljenih za posao da zaposli
    private static int distributeServantsForJob(int numberOfServents, int numberOfPoints) {

        if (numberOfPoints > numberOfServents) {
            return 1;
        } else {
            return numberOfPoints;
        }

    }

    //daje fraktalne id-ijeve
    private static List<String> generateFractalIdForJob(int numberOfServents) {

        List<String> fractals = new ArrayList<>();

        for (int i = 0; i<numberOfServents; i++) {
            fractals.add(String.valueOf(i));
        }

        return fractals;

    }

    //deljenje regiona
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


            //hocemo sivma da posaljemo direkt poruku
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
                    if (newFractals.size() == 1) {   //samo jedan servent izvrsava posao
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

        AppConfig.timestampedStandardPrint("Razlog za raspodelu posla: " + reason);

        List<Job> activeJobs = AppConfig.systemState.getSystemActiveJobs();
        Map<Job, Integer> jobServentCount = assignNumberOfServentsToJob(activeJobs);

        AppConfig.timestampedStandardPrint("Broj servenata za poslove: ");
        AppConfig.timestampedStandardPrint(String.valueOf(jobServentCount));

        List<Fractal> fractalList = new ArrayList<>();
        Map<Job, List<String>> jobFractalsMap = new HashMap<>();
        for (Map.Entry<Job, Integer> entry : jobServentCount.entrySet()) {

            //izracunavanje broja servenata potrebnih za izvrsavanje posla
            int distributedServentsNumber = distributeServantsForJob(entry.getValue(), entry.getKey().getPointsCount());
            AppConfig.timestampedStandardPrint("Broj uposljenih servenata za posao: " + entry.getKey().getName() + " - " + entry.getValue());

            //izracunavanje fraktalnih id-ijeva za trenutni posao
            List<String> fractalsForJob = generateFractalIdForJob(distributedServentsNumber);
            AppConfig.timestampedStandardPrint("Fraktali za posao: " + entry.getKey().getName() + " - " + fractalsForJob);
            jobFractalsMap.put(entry.getKey(), fractalsForJob);

            //dodavanje fraktalnih id-ijeva i poslova
            for (String fractalId : fractalsForJob) {
                fractalList.add(new Fractal(fractalId, entry.getKey().getName()));
            }
        }

        //deli fractalId-eve serventima redom, sve dok svaki fraktal id ne dodeli nekom serventu
        //map id -> fractal -> job

        Map<Integer, Fractal> newJobs = new HashMap<>();
        for (Map.Entry<Integer, ServentInfo> entry : AppConfig.systemState.getServentInfoMap().entrySet()) {
            newJobs.put(entry.getKey(), fractalList.remove(0));
            if (fractalList.isEmpty()) {
                break;
            }
        }
        AppConfig.systemState.setServentsJobsMap(newJobs);
        AppConfig.timestampedStandardPrint("Novi poslovi:");
        AppConfig.timestampedStandardPrint(newJobs.toString());


        //mapiranje stari fraktali sa novim fraktalima
        //za prenos vec izracunatih vrednosti ako su poslovi vec bili pokrenuti
        AppConfig.timestampedStandardPrint("Prethodni poslovi:");
        Map<Integer, Fractal> previousJobs = new HashMap<>(AppConfig.systemState.getServentsJobsMap());
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

        AppConfig.timestampedStandardPrint("Mapa za poslove fraktala: \n" + jobFractalsMap);

        for (Map.Entry<Job, List<String>> entry: jobFractalsMap.entrySet()) {
            Job currentJob = entry.getKey();
            List<String> fractals = entry.getValue();

            //izračunavanje pocetne podele posla i slanje poruke
            List<Point> jobPoints = currentJob.getPoints();
            double factor = currentJob.getProportion();

            //ako samo jedan čvor izvršava posao
            if (fractals.size() == 1) {
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
        // salje neaktivnim cvorovima da su neaktivni
        for (Map.Entry<Integer, ServentInfo> entry: AppConfig.systemState.getServentInfoMap().entrySet()) {
            int serventId = entry.getKey();

            if (!AppConfig.systemState.getServentsJobsMap().containsKey(serventId)) {
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

       //cekamo da drugi posalju poruke o izvrsavanju posla ili potvrde u mirovanju        AppConfig.timestampedStandardPrint("collecting ack from servents...");
        while (true) {
            if (AppConfig.systemState.getAckMsgCount().get() == serventCount) {
                break;
            }
        }
        AppConfig.timestampedStandardPrint("Svi serventi su poslali ack...");

        AppConfig.systemState.getAckMsgCount().set(0);
    }

    public enum JobScheduleReason {
        NEW_JOB_ADDED,
        JOB_REMOVED,
        NEW_SERVENT_ADDED,
        SERVENT_REMOVED
    }
}
