package telegram.scheduled;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import telegram.config.GeneralData;
import telegram.http.HTTP;
import telegram.models.JsonModel;

import java.util.HashMap;

public class SchedulerRequest implements Job {

    private static final Logger logger = LogManager.getLogger();
    private GeneralData generalData = GeneralData.getInstance();
    private HTTP http = HTTP.getInstance();

    @Override
    public void execute(JobExecutionContext context) {
        System.out.println("--------------------------------------------");
        System.out.println("Job Name: " + context.getJobDetail().getJobDataMap().get("jobName"));
        System.out.println("============================================");

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        HttpResponse httpResponse = http.schedulerRequest(jobDataMap);

        if (httpResponse == null || httpResponse.getStatus() == 401) {
            generalData.refreshToken();
            httpResponse = http.schedulerRequest(jobDataMap);
        }

        if (httpResponse == null || httpResponse.getStatus() != 200){
            logger.info("Problem in service by scheduled: " + jobDataMap.get("serverURL"));
            return;
        }

        JsonModel jsonModel;
        try {
            jsonModel = new Gson().fromJson(httpResponse.getBody().toString(), new TypeToken<JsonModel>() {}.getType());
        } catch (JsonSyntaxException e) {
            logger.info(e.getMessage());
            return;
        }

        HashMap<String, Object> respMessage = http.sendMessage((String) jobDataMap.get("botToken"), jsonModel);
        if ((Integer)respMessage.get("code") != 200) {
            logger.info(respMessage.get("error").toString());
            return;
        }
    }
}
