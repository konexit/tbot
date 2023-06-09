package dispatcher.telegramAPI.services;

import com.mashape.unirest.http.HttpResponse;
import dispatcher.http.HTTP;
import dispatcher.telegramAPI.config.TelegramConfig;
import dispatcher.telegramAPI.models.ResponseJsonModel;
import dispatcher.unit.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

public class TelegramService {

    private static TelegramService telegramService;
    private TelegramService() {}
    public static synchronized TelegramService getInstance() {
        if (telegramService == null) telegramService = new TelegramService();
        return telegramService;
    }

    private static final Logger logger = LogManager.getLogger();
    private TelegramConfig telegramConfig = TelegramConfig.getInstance();
    private HTTP http = HTTP.getInstance();

    public HashMap<String, Object> sendTelegramMessage(String botToken, ResponseJsonModel jsonModel) {
        HashMap<String, Object> resp = new HashMap<>();
        if (!(Boolean) jsonModel.getTelegramDispatcher().get("sendMessage")) {
            resp.put("code", 200);
            return resp;
        }

        ArrayList<String> chat_id = null;
        try {
            chat_id = (ArrayList<String>) jsonModel.getTelegramDispatcher().get("chat_id");
        } catch (Exception e){
            logger.warn("Problem with json from server to get chat_id");
        }

        if (chat_id != null) {
            chat_id.forEach((String chatId) -> jsonModel.getTelegram().forEach(message -> {
                message.put("chat_id", chatId);
                HttpResponse httpResponse = http.postRequest(telegramConfig.getPropertiesByKey("telegramURL") + "/bot" + botToken + "/sendMessage", Converter.convertObjectToJson(message));
                if (httpResponse == null || (httpResponse.getStatus() != 200 && httpResponse.getStatus() != 403) ) {
                    resp.put("code", 500);
                    resp.put("error", "Problem with telegram");
                }
            }));
        }

        if (!resp.containsKey("code")) resp.put("code", 200);
        return resp;
    }

}
