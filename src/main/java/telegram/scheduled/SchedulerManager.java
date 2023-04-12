//package telegram.scheduled;
//
//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;
//
//import java.util.Map;
//import java.util.Properties;
//
//public class SchedulerManager {
//
//    public void createScheduler(Properties properties, Object realisationJob, Boolean loop, String cron, Map jobData){
//        Scheduler scheduler = null;
//        try {
//            scheduler = new StdSchedulerFactory(properties).getScheduler();
//            scheduler.start();
//            JobBuilder jobBuilder = JobBuilder.newJob((Class<? extends Job>) realisationJob.getClass());
//            TriggerBuilder triggerBuilder = TriggerBuilder.newTrigger().startNow();
//
//            if (jobData != null)  jobBuilder.usingJobData(new JobDataMap(jobData));
//            if (loop) triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(cron));
//
//            scheduler.scheduleJob(jobBuilder.build(), triggerBuilder.build());
//
//        } catch (SchedulerException e) {
//            e.printStackTrace();
//        }
//
//    }
//}
