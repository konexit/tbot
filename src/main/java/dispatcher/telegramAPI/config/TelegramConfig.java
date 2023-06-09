package dispatcher.telegramAPI.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import dispatcher.telegramAPI.models.TelegramBotModel;
import dispatcher.unit.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class TelegramConfig {

    private static TelegramConfig telegramConfig;
    private TelegramConfig() {}
    public static synchronized TelegramConfig getInstance() {
        if (telegramConfig == null) telegramConfig = new TelegramConfig();
        return telegramConfig;
    }

    private static final Logger logger = LogManager.getLogger();
    private Map<String, Object> telegramApplicationProperties = new HashMap<>();
    private Map<String, TelegramBotModel> mapTelegramConfig = new HashMap<>();
    private Map<String, String> mapTelegramMetadata = new HashMap<>();


    public Boolean setTelegramConfig(JsonNode telegramConfigData){
        if (telegramConfigData == null){
            logger.warn("Cannot find key telegram in telegramBotConfig file");
            return false;
        }

        Map<String, TelegramBotModel> telegramConfig =
                (HashMap<String, TelegramBotModel>) Converter.convertObjectToSpecificObject(telegramConfigData,
                        new TypeReference<HashMap<String, TelegramBotModel>>(){});

        if (telegramConfig == null) {
            logger.warn("Cannot convert json telegram config to HashMap");
            return false;
        }

        for (Map.Entry<String, TelegramBotModel> entry : telegramConfig.entrySet()) {
            mapTelegramMetadata.put(entry.getValue().getName(), entry.getKey());
        }

        mapTelegramConfig = telegramConfig;
        return true;
    }

    public Boolean setTelegramApplicationProperties(Properties prop){
        try {
            telegramApplicationProperties.put("telegramURL", prop.getProperty("telegram.URL"));
            telegramApplicationProperties.put("telegramRedirectURL", prop.getProperty("telegram.redirect.URL"));
            return true;
        } catch (NumberFormatException e){
            logger.warn("Cannot convert telegram property to some object EXCEPTION! " + e.getMessage());
            return false;
        }
    }

    public Map<String, TelegramBotModel> getMapTelegramConfig() {
        return mapTelegramConfig;
    }

    public Object getPropertiesByKey(String key) {
        return telegramApplicationProperties.get(key);
    }

    public TelegramBotModel getTelegramBotConfig(String botToken){
        return mapTelegramConfig.get(botToken);
    }

    public String getTokenByBotName(String botName){
        return mapTelegramMetadata.get(botName);
    }
}