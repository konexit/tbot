package telegram.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import telegram.http.HTTP;
import telegram.models.TelegramBotModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class GeneralData {

    private static final Logger logger = LogManager.getLogger();
    private Map<String, TelegramBotModel> mapTelegramBot = new HashMap<>();
    private String accessToken;
    private String telegramURL;
    private String authTokenURL;
    private String ckEditorCredentials;

    private static GeneralData generalData;
    private GeneralData() {}
    public static GeneralData getInstance() {
        if (generalData == null) generalData = new GeneralData();
        return generalData;
    }

    public void config() {
        try (InputStream input = new FileInputStream(System.getProperty("user.dir") + File.separator  + "application.properties")) {
            Properties prop = new Properties();
            prop.load(input);
            telegramURL = prop.getProperty("telegram.URL");
            authTokenURL = prop.getProperty("AuthToken.URL");
            ckEditorCredentials = prop.getProperty("ckEditor.Credentials");
            init();
        } catch (IOException ex) {
            logger.info("Problem with application.properties file");
        }
    }


    public void refreshToken(){
        HttpResponse response = HTTP.getInstance().postRequest(this.authTokenURL + "/login", this.ckEditorCredentials);
        Map<String, String> body = new Gson().fromJson(response.getBody().toString(), new TypeToken<HashMap<String, String>>() {}.getType());
        accessToken = body.get("access_token");
    }

    public void refreshTelegramConfig() {
        getTasksFromConfigFile();
    }

    public TelegramBotModel getTelegramBot(String tokenBot) {
        return mapTelegramBot.get(tokenBot);
    }

    public Map<String, TelegramBotModel> getMapTelegramBot() {
        return mapTelegramBot;
    }

    public String getTelegramURL() {
        return telegramURL;
    }

    public String getAccessToken() {
        return accessToken;
    }

    private void init(){
        refreshToken();
        refreshTelegramConfig();
    }

    private void getTasksFromConfigFile(){
        ObjectMapper mapper = new ObjectMapper();
        JsonNode telegramConfig = null;
        try {
            telegramConfig = mapper.readValue(getJsonFromFileTelegramConfig(), JsonNode.class);
        } catch (JsonProcessingException e) {
            logger.info("Cannot convert json in telegramBotConfig file");
        }

        if (telegramConfig != null && telegramConfig.get("tasks") != null) {
            try {
                mapTelegramBot = mapper.convertValue(telegramConfig.get("tasks"),  new TypeReference<HashMap<String, TelegramBotModel>>(){});
            } catch (Exception e){
                logger.info("Cannot convert json of tasks");
            }
        } else {
            logger.info("Cannot find key task in telegramBotConfig file");
        }
    }


    private String getJsonFromFileTelegramConfig(){
        String fileContent = "";
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator  + "telegramBotConfig.json"));
            fileContent = new String (bytes);
        } catch (Exception e) {
            logger.info("Cannot read file telegramBotConfig");
        }
        return fileContent;
    }
}