package dispatcher.scheduled.models;

import java.util.Map;

public class JobConfigModel {

    private String schedule;
    private String jobName;
    private Boolean jobState =  false;
    private Boolean loopExecute = false;
    private String className;
    private Map<String, Object> request;

    public JobConfigModel() {}

    public String getSchedule() {
        return schedule;
    }

    public String getJobName() {
        return jobName;
    }

    public Boolean getJobState() {
        return jobState;
    }

    public Boolean getLoopExecute() {
        return loopExecute;
    }

    public String getClassName() {
        return className;
    }

    public Map<String, Object> getRequest() {
        return request;
    }

}
