package dispatcher.telegramAPI.services;

import dispatcher.scheduled.ScheduledAPI;
import dispatcher.telegramAPI.config.TelegramConfig;
import dispatcher.telegramAPI.models.TelegramBotModel;

import java.util.Map;

public class TelegramScheduled {

    private static TelegramScheduled telegramScheduled;
    private TelegramScheduled() {}
    public static synchronized TelegramScheduled getInstance() {
        if (telegramScheduled == null) telegramScheduled = new TelegramScheduled();
        return telegramScheduled;
    }

    private TelegramConfig telegramConfig = TelegramConfig.getInstance();
    private ScheduledAPI scheduledAPI = ScheduledAPI.getInstance();

    public void addTelegramJobs(){
        Map<String, TelegramBotModel> telegramBotModelsMap = telegramConfig.getMapTelegramConfig();

        if (telegramBotModelsMap != null && telegramBotModelsMap.size() > 0) {
            telegramBotModelsMap.forEach((String botToken, TelegramBotModel telegramBotModel) -> {
                if (telegramBotModel.getState() != null && telegramBotModel.getState() && telegramBotModel.getJobsConfig() != null) {
                    telegramBotModel.getJobsConfig().forEach(telegramJob -> {
                        if (telegramJob.getJobState() && telegramJob.getRequest() != null) {
                            scheduledAPI.addJob(telegramBotModel.getName(), telegramJob, telegramJob.getRequest());
                        }
                    });
                }
            });
        }
    }

}
