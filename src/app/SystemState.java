package app;

import app.model.Fractal;
import app.model.Job;
import app.model.ServentInfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemState {

    private Map<Integer, ServentInfo> serventInfoMap;
    private Map<Integer, Fractal> serventsJobsMap;
    private List<Job> systemActiveJobs;

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

    public SystemState(ServentInfo me) {
        serventInfoMap.put(me.getId(), me);
    }

    public void setServentInfoMap(Map<Integer, ServentInfo> serventInfoMap) {
        this.serventInfoMap = serventInfoMap;
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

}
