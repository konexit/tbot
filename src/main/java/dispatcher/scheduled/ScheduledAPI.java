package dispatcher.scheduled;

import dispatcher.scheduled.models.JobConfigModel;
import dispatcher.scheduled.services.ScheduledService;

import java.util.Map;

public class ScheduledAPI {

    private static ScheduledAPI scheduledAPI;
    private ScheduledAPI() {}
    public static synchronized ScheduledAPI getInstance() {
        if (scheduledAPI == null) scheduledAPI = new ScheduledAPI();
        return scheduledAPI;
    }

    private ScheduledService scheduledService = ScheduledService.getInstance();

    public Boolean run(){
        return scheduledService.run();
    }

    public Boolean addJob(String jobGroup, JobConfigModel jobConfigModel, Map<String, Object> data){
        return scheduledService.addJob(jobGroup, jobConfigModel, data);
    }

    public Boolean deleteJob(String groupName, String jobName){
        return scheduledService.deleteJob(groupName, jobName);
    }

    public String getInfoJobs() {
        return scheduledService.getInfoJobs();
    }
}