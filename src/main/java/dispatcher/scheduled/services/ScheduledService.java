package dispatcher.scheduled.services;

import dispatcher.scheduled.models.JobConfigModel;
import dispatcher.unit.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.quartz.impl.triggers.SimpleTriggerImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ScheduledService {

    private static ScheduledService scheduledService;
    private ScheduledService() {}
    public static synchronized ScheduledService getInstance() {
        if (scheduledService == null) scheduledService = new ScheduledService();
        return scheduledService;
    }

    private static final Logger logger = LogManager.getLogger();
    private Scheduler scheduler;

    public Boolean run(){
        try {
            scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            return true;
        } catch (SchedulerException e) {
            logger.error("Cannot init Scheduler EXCEPTION: " + e.getMessage());
            return false;
        }
    }

    public Boolean addJob(String jobGroup, JobConfigModel jobConfigModel, Map<String, Object> data){
        try {
            JobBuilder jobBuilder = JobBuilder.newJob((Class<? extends Job>) getClassByString(jobConfigModel.getClassName()));
            TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().startNow();

            if (jobConfigModel.getJobName() != null){
                jobBuilder.withIdentity(jobConfigModel.getJobName(), jobGroup);
                triggerBuilder.withIdentity(jobConfigModel.getJobName(), jobGroup);
            }

            jobBuilder.usingJobData(new JobDataMap(data));

            if (jobConfigModel.getLoopExecute()) triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(jobConfigModel.getSchedule()));

            scheduler.scheduleJob(jobBuilder.build(), triggerBuilder.build());

            return true;
        } catch (Exception e) {
            logger.warn("Cannot add job to scheduler EXCEPTION: " + e.getMessage());
            return false;
        }
    }

    public Boolean deleteJob(String groupName, String jobName){
        try {
            for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {
                if (jobKey.getName().equals(jobName)) return scheduler.deleteJob(jobKey);
            }
            return false;
        } catch (SchedulerException e) {
            logger.warn("Cannot delete job from scheduler EXCEPTION: " + e.getMessage());
            return false;
        }
    }

    public String getInfoJobs(){
        String json = "{}";
        try {
            Map<String, Object> dataGroup = new HashMap<>();
            for (String groupName : scheduler.getJobGroupNames()) {

                List<Map<String, Object>> jobs = new LinkedList<>();
                for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(groupName))) {

                    Map<String, Object> job = new HashMap<>();
                    List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
                    Trigger trigger = triggers.get(0);
                    job.put("jobName", jobKey.getName());
                    job.put("schedule", (trigger instanceof CronTriggerImpl) ? ((CronTriggerImpl) trigger).getCronExpression() : "");
                    job.put("startTime", trigger.getStartTime() != null ? trigger.getStartTime() : 0);
                    job.put("endTime", trigger.getEndTime() != null ? trigger.getEndTime() : 0 );
                    job.put("nextFireTime", trigger.getNextFireTime() != null ? trigger.getNextFireTime() : 0);
                    job.put("previousFireTime", trigger.getPreviousFireTime() != null ? trigger.getPreviousFireTime() : 0);
                    job.put("priority", trigger.getPriority());
                    job.put("timesTriggered", (trigger instanceof SimpleTriggerImpl) ? ((SimpleTriggerImpl) trigger).getTimesTriggered() : 0);
                    jobs.add(job);
                }

                dataGroup.put(groupName, jobs);
            }

            json = Converter.convertObjectToJson(dataGroup);

            if (json == null) {
                logger.warn("Cannot convert object of jobs to json");
                json = "{}";
            }

        } catch (Exception e) {
            logger.warn("Cannot get info jobs from scheduler EXCEPTION: " + e.getMessage());
        }

        return json;
    }

    private Class getClassByString(String className){
        Class jobRealisation = null;
        try {
            return Class.forName(className);
        } catch (Exception e) {
            logger.warn("Cannot find class name for scheduler EXCEPTION! " + e.getMessage());
        }
        return jobRealisation;
    }
}
