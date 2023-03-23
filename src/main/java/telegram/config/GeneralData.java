package telegram.config;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
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

    private Map<String, TelegramBotModel> listTelegramBot;
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
            refreshToken();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        refreshTelegramConfig();
    }

    public void refreshToken(){
        HttpResponse response = HTTP.getInstance().postRequest(this.authTokenURL + "/login", this.ckEditorCredentials);
        Map<String, String> body = new Gson().fromJson(response.getBody().toString(), new TypeToken<HashMap<String, String>>() {}.getType());
        accessToken = body.get("access_token");
    }

    public void refreshTelegramConfig() {
        listTelegramBot = new Gson().fromJson(getJsonFromFileTelegramConfig(), new TypeToken<HashMap<String, TelegramBotModel>>() {}.getType());
    }

    public TelegramBotModel getListTelegramBot(String tokenBot) {
        return listTelegramBot.get(tokenBot);
    }

    public String getTelegramURL() {
        return telegramURL;
    }

    public String getAccessToken() {
        return accessToken;
    }

    private String getJsonFromFileTelegramConfig(){
        String fileContent = "";
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator  + "telegramBotConfig.txt"));
            fileContent = new String (bytes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent;
    }
}