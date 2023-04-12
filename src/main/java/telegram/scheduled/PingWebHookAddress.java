package telegram.scheduled;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import telegram.config.GeneralData;
import telegram.http.HTTP;
import telegram.models.TelegramBotModel;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class PingWebHookAddress implements Job{

    private static final Logger logger = LogManager.getLogger();
    private GeneralData generalData = GeneralData.getInstance();

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        Queue<String> addresses = (LinkedList<String>) jobDataMap.get("ipAddresses");

        for (String address : addresses){
            try {
                InetAddress inetAddress = InetAddress.getByName(address);
                if (inetAddress.isReachable(1000)) {
                    if (!generalData.getActiveWebHookIP().equals(address)){
                        generalData.setActiveWebHookIP(address);
                        setIpAddressForWebHook(address);
                    }
                    break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setIpAddressForWebHook(String address){
        Map<String, TelegramBotModel> telegramBotModelsMap = generalData.getMapTelegramBot();
        if (telegramBotModelsMap != null && telegramBotModelsMap.size() > 0) {
            telegramBotModelsMap.forEach((String botToken, TelegramBotModel telegramBotModel) -> {
                if (telegramBotModel.getState() != null && telegramBotModel.getState()){
                    try {
                        Unirest.get(generalData.getTelegramURL() + "/" + botToken + "/setWebhook?ip_address=" + address + "&url=https://konex.com.ua:8443/getUpdates/?bot=" + botToken).asString();
                    } catch (UnirestException e) {
                        logger.warn("Cannot set ip address to webhook EXCEPTION! " + e.getMessage());
                    }
                }
            });
        }
    }
}
