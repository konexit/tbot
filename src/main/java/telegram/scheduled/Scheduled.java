//package telegram.scheduled;
//
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.ScheduledFuture;
//import java.util.concurrent.TimeUnit;
//
//public class Scheduled {
//
//
//    public static void startScheduled() {
//
//        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
//
//        // init Delay = 5, repeat the task every 1 second
//        ScheduledFuture<?> scheduledFuture = ses.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                metersAlertMessge();
//            }
//        }, 5, 1, TimeUnit.SECONDS);
//    }
//
//    private void metersAlertMessge(){
//
//    }
//}
