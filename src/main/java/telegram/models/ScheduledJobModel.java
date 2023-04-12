package telegram.models;

import java.util.Map;

public class ScheduledJobModel {

    private String schedule;
    private String jobName;
    private Boolean jobState;
    private Boolean loopExecute;
    private String className;
    private Map<String, Object> request;

    public ScheduledJobModel() {}

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
