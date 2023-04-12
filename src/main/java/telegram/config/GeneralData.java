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
import telegram.models.ScheduledJobModel;
import telegram.models.TelegramBotModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class GeneralData {

    private static final Logger logger = LogManager.getLogger();
    private Map<String, TelegramBotModel> mapTelegramBot = new HashMap<>();
    private Map<String, Object> system = new HashMap<>();
    private String domain = "";
    private int serverPort;
    private int schedulerThreadCount;
    private String accessToken = "";
    private String telegramURL = "";
    private String authTokenURL = "";
    private String activeWebHookIP = "";
    private String ckEditorCredentials = "";


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
            domain = prop.getProperty("domain");
            serverPort = Integer.parseInt(prop.getProperty("server.port"));
            schedulerThreadCount = Integer.parseInt(prop.getProperty("scheduler.threadCount"));
            telegramURL = prop.getProperty("telegram.URL");
            authTokenURL = prop.getProperty("AuthToken.URL");
            ckEditorCredentials = prop.getProperty("ckEditor.Credentials");
            init();
        } catch (Exception ex) {
            logger.fatal("Problem with application.properties file");
        }
    }

    public void refreshToken(){
        HttpResponse response = HTTP.getInstance().postRequest(this.authTokenURL + "/login", this.ckEditorCredentials);

        if (response == null || response.getStatus() != 200) {
            logger.info("Problem with authorization service");
        }

        try {
            Map<String, String> body = new Gson().fromJson(response.getBody().toString(), new TypeToken<HashMap<String, String>>() {}.getType());
            accessToken = body.get("access_token");
        } catch (Exception e){
            logger.error("Problem with json from authorization service");
        }
    }

    public void refreshTelegramConfig() {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode telegramConfig = null;
        try {
            telegramConfig = mapper.readValue(getJsonFromFileTelegramConfig(), JsonNode.class);
        } catch (JsonProcessingException e) {
            logger.warn("Cannot convert json in telegramBotConfig file");
        }
        if (telegramConfig != null ) {
            getTelegramJobs(telegramConfig);
            getSystemJobs(telegramConfig);
        } else logger.warn("TelegramBotConfig file is empty");
    }

    public TelegramBotModel getTelegramBot(String tokenBot) {
        return mapTelegramBot.get(tokenBot);
    }

    public Map<String, Object> getSystem() {
        return system;
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

    public String getDomain() {
        return domain;
    }

    public int getServerPort() {
        return serverPort;
    }

    public String getActiveWebHookIP() {
        return activeWebHookIP;
    }

    public void setActiveWebHookIP(String activeWebHookIP) {
        this.activeWebHookIP = activeWebHookIP;
    }

    public int getSchedulerThreadCount() {
        return schedulerThreadCount;
    }

    private void init(){
        refreshToken();
        refreshTelegramConfig();
    }


    private void getTelegramJobs(JsonNode telegramConfig){
        ObjectMapper mapper = new ObjectMapper();
        if (telegramConfig.get("telegram") != null) {
            try {
                mapTelegramBot = mapper.convertValue(telegramConfig.get("telegram"),  new TypeReference<HashMap<String, TelegramBotModel>>(){});
            } catch (Exception e){
                logger.warn("Cannot convert json of telegram from telegramBotConfig file");
            }
        } else {
            logger.warn("Cannot find key telegram in telegramBotConfig file");
        }
    }

    private void getSystemJobs(JsonNode telegramConfig){
        ObjectMapper mapper = new ObjectMapper();
        if (telegramConfig.get("system") != null) {
            try {
                system = mapper.convertValue(telegramConfig.get("system"),  new TypeReference<HashMap<String, Object>>(){});
            } catch (Exception e){
                logger.warn("Cannot convert json of systemJobs from telegramBotConfig file");
            }
        } else {
            logger.warn("Cannot find key systemJobs in telegramBotConfig file");
        }
    }

    private String getJsonFromFileTelegramConfig(){
        String fileContent = "";
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator  + "telegramBotConfig.json"));
            fileContent = new String (bytes);
        } catch (Exception e) {
            logger.fatal("Cannot read file telegramBotConfig");
        }
        return fileContent;
    }

//    private void pingCommunicationChannel(String cron, String ipAddresses) {
//        SchedulerManager schedulerManager = new SchedulerManager();
//        Properties properties = new Properties();
//        properties.setProperty("org.quartz.threadPool.threadCount", "1");
//        schedulerManager.createScheduler(properties, new PingWebHookAddress(), true,  cron,
//                new HashMap<String, Object>() {{ put("ipAddresses", new LinkedList<>(Arrays.asList(ipAddresses.split(", ")))); }});
//    }
}