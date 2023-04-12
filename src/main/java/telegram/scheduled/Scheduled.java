package telegram.scheduled;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import telegram.config.GeneralData;
import telegram.models.ScheduledJobModel;
import telegram.models.TelegramBotModel;

import java.util.List;
import java.util.Map;

public class Scheduled {

    private static final Logger logger = LogManager.getLogger();
    private GeneralData generalData = GeneralData.getInstance();

    private static Scheduled scheduled;
    private Scheduled() {}
    public static synchronized Scheduled getInstance() {
        if (scheduled == null) scheduled = new Scheduled();
        return scheduled;
    }

    public void startJobs() {
        try {
            Scheduler scheduler = new StdSchedulerFactory().getScheduler();
            scheduler.start();
            setTelegramJobs(scheduler);
            setSystemJobs(scheduler);
        } catch (SchedulerException e) {
            logger.error("Cannot start Scheduler EXCEPTION: " + e.getMessage());
        }

    }

    private void setTelegramJobs(Scheduler scheduler) {
        Map<String, TelegramBotModel> telegramBotModelsMap = generalData.getMapTelegramBot();

        if (telegramBotModelsMap != null && telegramBotModelsMap.size() > 0) {
            telegramBotModelsMap.forEach((String botToken, TelegramBotModel telegramBotModel) -> {
                if (telegramBotModel.getState() != null && telegramBotModel.getState() && telegramBotModel.getJobsConfig() != null) {
                    telegramBotModel.getJobsConfig().forEach(telegramJob -> {
                        if (telegramJob.getJobState() && telegramJob.getRequest() != null) {
                            setScheduler(scheduler, telegramJob, telegramBotModel.getName());
                        }
                    });
                }
            });
        }
    }

    private void setSystemJobs(Scheduler scheduler) {
        Map<String, Object> system = generalData.getSystem();

        List<ScheduledJobModel> systemJobList = (List<ScheduledJobModel>) system.get("jobsConfig");
        if (systemJobList != null && systemJobList.size() > 0) {
            systemJobList.forEach(systemJobModel -> {
                setScheduler(scheduler, systemJobModel, "system");
            });
        }
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

    private void setScheduler(Scheduler scheduler, ScheduledJobModel job, String nameGroupJob){
        try {
            JobBuilder jobBuilder = JobBuilder.newJob((Class<? extends Job>) getClassByString(job.getClassName()));
            TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().startNow();

            if (job.getJobName() != null){
                jobBuilder.withIdentity(job.getJobName(), nameGroupJob);
                triggerBuilder.withIdentity(job.getJobName(), nameGroupJob);
            }

            jobBuilder.usingJobData(new JobDataMap(job.getRequest()));

            if (job.getLoopExecute()) triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(job.getSchedule()));

            scheduler.scheduleJob(jobBuilder.build(), triggerBuilder.build());
        } catch (Exception e) {
            logger.warn("Cannot add job to scheduler EXCEPTION: " + e.getMessage());
        }
    }
}