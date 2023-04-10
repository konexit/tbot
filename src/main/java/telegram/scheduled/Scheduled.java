package telegram.scheduled;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import telegram.config.GeneralData;
import telegram.models.TelegramBotModel;

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

        Map<String, TelegramBotModel> telegramBotModelsMap = generalData.getMapTelegramBot();

        if (telegramBotModelsMap != null && telegramBotModelsMap.size() > 0) {
            try {
                Scheduler scheduler = new StdSchedulerFactory().getScheduler();
                scheduler.start();
                telegramBotModelsMap.forEach((String botToken, TelegramBotModel telegramBotModel) -> {
                    if (telegramBotModel.getState() != null && telegramBotModel.getState() && telegramBotModel.getJobsConfig() != null) {
                        telegramBotModel.getJobsConfig().forEach(job -> {
                            if (job.get("jobState") != null && (Boolean) job.get("jobState") && job.get("request") != null) {
                                try {
                                    JobBuilder jobBuilder = JobBuilder.newJob(SchedulerRequest.class);
                                    TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().startNow();

                                    if (job.get("jobName") != null && telegramBotModel.getName() != null){
                                        jobBuilder.withIdentity((String) job.get("jobName"), telegramBotModel.getName());
                                        triggerBuilder.withIdentity((String) job.get("jobName"), telegramBotModel.getName());
                                    }

                                    jobBuilder.usingJobData(new JobDataMap((Map<String, Object>) job.get("request")) {{ put("botToken", botToken);  put("jobName", job.get("jobName")); }});

                                    if (job.get("loopExecute") != null && (Boolean) job.get("loopExecute") && job.get("schedule") != null) triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule((String) job.get("schedule")));

                                    scheduler.scheduleJob(jobBuilder.build(), triggerBuilder.build());
                                } catch (Exception e) {
                                    logger.warn("Cannot add job to scheduler EXCEPTION: " + e.getMessage());
                                }
                            }
                        });
                    }
                });
            } catch (SchedulerException e) {
                logger.error("Cannot start Scheduler EXCEPTION: " + e.getMessage());
            }
        }
    }
}