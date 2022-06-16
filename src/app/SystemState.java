package app;

import app.model.Fractal;
import app.model.Job;
import app.model.Point;
import app.model.ServentInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SystemState {

    private Map<Integer, ServentInfo> serventInfoMap = new HashMap<>();
    private Map<Integer, Fractal> serventsJobsMap = new HashMap<>();
    private List<Job> systemActiveJobs = new ArrayList<>();

    private WorkingJobInstance executionJob;

    private AtomicInteger ackMsgCount = new AtomicInteger(0);

    private final List<Point> receivedComputedPoints = new ArrayList<>();
    private final AtomicInteger calculatedPointsMsgCount = new AtomicInteger(0);
    private final AtomicInteger totalPointMsgExpected = new AtomicInteger(0);

    public Map<Integer, ServentInfo> getServentInfoMap() {
        return serventInfoMap;
    }

    public void setServentInfoMap(List<ServentInfo> serventInfo) {
        if (serventInfoMap == null) {
            serventInfoMap = new HashMap<>();
        }

        for (ServentInfo i : serventInfo) {
            serventInfoMap.put(i.getId(), i);
        }
    }

    //todo ovo se nedje duplo poziva
    public boolean addJob(Job newJob) {
        if(systemActiveJobs.contains(newJob)) {
            return false;
        } else {
            systemActiveJobs.add(newJob);
            return true;
        }
    }

    public void removeJob(String jobName) {
        for (Job job: systemActiveJobs) {
            if (job.getName().equals(jobName)) {
                systemActiveJobs.remove(job);
                break;
            }
        }

        List<Integer> ids = new ArrayList<>();
        for (Map.Entry<Integer, Fractal> entry: serventsJobsMap.entrySet()) {
            if (entry.getValue().getJob().equals(jobName)) {
                ids.add(entry.getKey());
            }
        }

        for (Integer id: ids) {
            serventsJobsMap.remove(id);
        }
    }

    public List<Integer> getAllServentIdsForJob(String jobName) {
        List<Integer> serventIdList = new ArrayList<>();
        for (Map.Entry<Integer, Fractal> entry: serventsJobsMap.entrySet()) {
            if (entry.getValue().getJob().equals(jobName)) {
                serventIdList.add(entry.getKey());
            }
        }
        return serventIdList;
    }

    public int getServentIdForFractal(Fractal fractal) {
        for (Map.Entry<Integer, Fractal> i : serventsJobsMap.entrySet()) {
            if (fractal.getJob().equals(i.getValue().getJob()) && fractal.getId().equals(i.getValue().getId())) {
                return i.getKey();
            }
        }

        return -1;
    }

    public ServentInfo getServentById(int id) {
        for (ServentInfo i: serventInfoMap.values()) {
            if (i.getId() == id) {
                return i;
            }
        }

        return null;
    }

    public void initializeSystemState(ServentInfo me) {
        serventInfoMap.put(me.getId(), me);

        try {
            Socket bsSocket = new Socket(AppConfig.BOOTSTRAP_IP_ADDRESS, AppConfig.BOOTSTRAP_PORT);

            PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
            bsWriter.write(
                    "New\n" +
                            AppConfig.myServentInfo.getIpAddress() + "\n" +
                            AppConfig.myServentInfo.getListenerPort() + "\n"
            );
            bsWriter.flush();
            bsSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setServentInfoMap(Map<Integer, ServentInfo> serventInfoMap) {
        this.serventInfoMap.putAll(serventInfoMap);
    }

    public List<Job> getSystemActiveJobs() {
        return systemActiveJobs;
    }

    public void setSystemActiveJobs(List<Job> systemActiveJobs) {
        this.systemActiveJobs = systemActiveJobs;
    }

    public Map<Integer, Fractal> getServentsJobsMap() {
        return serventsJobsMap;
    }

    public void setServentsJobsMap(Map<Integer, Fractal> serventsJobsMap) {
        this.serventsJobsMap = serventsJobsMap;
    }

    public AtomicInteger getAckMsgCount() {
        return ackMsgCount;
    }

    public void setAckMsgCount(AtomicInteger ackMsgCount) {
        this.ackMsgCount = ackMsgCount;
    }

    public void resetAfterReceivedComputedPoints() {
        receivedComputedPoints.clear();
        calculatedPointsMsgCount.set(0);
        totalPointMsgExpected.set(0);
    }

    public WorkingJobInstance getExecutionJob() {
        return executionJob;
    }

    public void setExecutionJob(WorkingJobInstance executionJob) {
        this.executionJob = executionJob;
    }

    public List<Point> getReceivedComputedPoints() {
        return receivedComputedPoints;
    }

    public AtomicInteger getCalculatedPointsMsgCount() {
        return calculatedPointsMsgCount;
    }

    public AtomicInteger getTotalPointMsgExpected() {
        return totalPointMsgExpected;
    }

    public void addComputedPoints(List<Point> newPoints) {
        receivedComputedPoints.addAll(newPoints);
        calculatedPointsMsgCount.getAndIncrement();
    }

}
