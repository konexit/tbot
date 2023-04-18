package dispatcher.telegramAPI.services;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import dispatcher.http.HTTP;
import dispatcher.system.SystemAPI;
import dispatcher.telegramAPI.TelegramAPI;
import dispatcher.telegramAPI.models.ResponseJsonModel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.HashMap;

public class TelegramJob implements Job {

    private static final Logger logger = LogManager.getLogger();
    private HTTP http = HTTP.getInstance();
    private TelegramAPI telegramAPI = TelegramAPI.getInstance();
    private SystemAPI systemAPI = SystemAPI.getInstance();

    @Override
    public void execute(JobExecutionContext context) {

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        HttpResponse httpResponse = http.requestByJobDataMap(jobDataMap, systemAPI.getDispatcherAuthToken());

        if (httpResponse == null || httpResponse.getStatus() == 401) {
            systemAPI.refreshDispatcherToken();
            httpResponse = http.requestByJobDataMap(jobDataMap, systemAPI.getDispatcherAuthToken());
        }

        if (httpResponse == null || httpResponse.getStatus() != 200){
            logger.info("Problem in service by scheduled: " + jobDataMap.get("serverURL"));
            return;
        }

        ResponseJsonModel jsonModel;
        try {
            jsonModel = new Gson().fromJson(httpResponse.getBody().toString(), new TypeToken<ResponseJsonModel>() {}.getType());
        } catch (JsonSyntaxException e) {
            logger.info("Cannot convert response json to jsonModel EXCEPTION: " + e.getMessage());
            return;
        }

        HashMap<String, Object> respMessage = telegramAPI.sendTelegramMessage((String) jobDataMap.get("botToken"), jsonModel);
        if ((Integer)respMessage.get("code") != 200) {
            logger.info(respMessage.get("error").toString());
            return;
        }
    }
}
