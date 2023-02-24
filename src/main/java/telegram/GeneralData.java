package telegram;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import telegram.models.TelegramBotModel;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class GeneralData {

    private static Logger logger = Logger.getLogger(GeneralData.class.getName());

    private Map<String, TelegramBotModel> listTelegramBot;

    private String accessToken = "f345tg34.3453grgyu576.34543teteru";
    private String telegramURL;

    PropertiesConfiguration config = new PropertiesConfiguration();

    private static GeneralData generalData;
    private GeneralData() {}
    public static GeneralData getInstance() {
        if (generalData == null) generalData = new GeneralData();
        return generalData;
    }

    public void start() {
        refreshTelegramConfig();
        try {
            config.load("application.properties");
            telegramURL = config.getString("telegram.URL");
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public void refreshTelegramConfig() {
        listTelegramBot = new Gson().fromJson(getJsonFromFileTelegramConfig(), new TypeToken<HashMap<String, TelegramBotModel>>() {}.getType());
    }

    private String getJsonFromFileTelegramConfig(){
        String fileContent = "";
        try {
            //todo get file by relative path
            byte[] bytes = Files.readAllBytes(Paths.get("telegramBotConfig.txt"));
            fileContent = new String (bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    public String getTelegramURL() {
        return telegramURL;
    }

    public TelegramBotModel getListTelegramBot(String tokenBot) {
        return listTelegramBot.get(tokenBot);
    }

    public String getAccessToken() {
        return accessToken;
    }

}
