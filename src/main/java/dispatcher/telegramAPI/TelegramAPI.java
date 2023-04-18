package dispatcher.telegramAPI;

import com.fasterxml.jackson.databind.JsonNode;
import dispatcher.telegramAPI.config.TelegramConfig;
import dispatcher.telegramAPI.models.ResponseJsonModel;
import dispatcher.telegramAPI.models.TelegramBotModel;
import dispatcher.telegramAPI.services.TelegramScheduled;
import dispatcher.telegramAPI.services.TelegramService;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TelegramAPI {

    private static TelegramAPI telegramAPI;
    private TelegramAPI() {}
    public static synchronized TelegramAPI getInstance() {
        if (telegramAPI == null) telegramAPI = new TelegramAPI();
        return telegramAPI;
    }

    private TelegramScheduled telegramScheduled = TelegramScheduled.getInstance();
    private TelegramConfig telegramConfig = TelegramConfig.getInstance();
    private TelegramService telegramService = TelegramService.getInstance();

    public void addTelegramJobs() {
        telegramScheduled.addTelegramJobs();
    }

    public HashMap<String, Object> sendTelegramMessage(String botToken, ResponseJsonModel jsonModel){
        return telegramService.sendTelegramMessage(botToken, jsonModel);
    }

    public Boolean setTelegramConfig(JsonNode telegramConfigData){
        return telegramConfig.setTelegramConfig(telegramConfigData);
    }

    public Boolean setTelegramApplicationProperties(Properties prop){
        return telegramConfig.setTelegramApplicationProperties(prop);
    }

    public Map<String, TelegramBotModel> getMapTelegramBot() {
        return telegramConfig.getMapTelegramConfig();
    }

    public Object getPropertiesByKey(String key) {
        return telegramConfig.getPropertiesByKey(key);
    }

    public TelegramBotModel getTelegramBotConfig(String botToken){
        return telegramConfig.getTelegramBotConfig(botToken);
    }

}