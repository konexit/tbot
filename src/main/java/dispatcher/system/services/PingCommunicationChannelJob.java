package dispatcher.system.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mashape.unirest.http.HttpResponse;
import dispatcher.http.HTTP;
import dispatcher.system.SystemAPI;
import dispatcher.telegramAPI.TelegramAPI;
import dispatcher.telegramAPI.models.TelegramBotModel;
import dispatcher.unit.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class PingCommunicationChannelJob implements Job {

    private static final Logger logger = LogManager.getLogger();

    private SystemAPI systemAPI = SystemAPI.getInstance();
    private TelegramAPI telegramAPI = TelegramAPI.getInstance();
    private HTTP http = HTTP.getInstance();

    @Override
    public void execute(JobExecutionContext context) {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        Queue<String> addresses =
                (LinkedList<String>) Converter.convertObjectToSpecificObject(jobDataMap.get("ipAddresses"),
                        new TypeReference<LinkedList<String>>() {});

        if (addresses == null) {
            logger.warn("Cannot convert json ip addresses to LinedList");
            return;
        }

        for (String address : addresses) {
            try {
                InetAddress inetAddress = InetAddress.getByName(address);
                if (inetAddress.isReachable(1000)) {
                    if (!systemAPI.getActiveCommunicationChannel().equals(address)) {
                        systemAPI.setActiveCommunicationChannel(address);
                        setIpAddressForWebHook(address);
                    }
                    break;
                }
            } catch (IOException e) {
                logger.info("IP address " + address + " is not working EXCEPTION! " + e.getMessage());
            }
        }
    }

    private void setIpAddressForWebHook(String address) {
        Map<String, TelegramBotModel> telegramBotModelsMap = telegramAPI.getMapTelegramBot();

        if (telegramBotModelsMap == null && telegramBotModelsMap.size() > 0) {
            telegramBotModelsMap.forEach((String botToken, TelegramBotModel telegramBotModel) -> {
                if (telegramBotModel.getState() != null && telegramBotModel.getState()) {
                    HttpResponse response = http.getRequest(telegramAPI.getPropertiesByKey("telegramURL") + "/bot" + botToken
                            + "/setWebhook?ip_address=" + address
                            + "&url=" + telegramAPI.getPropertiesByKey("telegramRedirectURL") + "/getUpdates/?bot=" + botToken);
                    if (response == null || response.getStatus() != 200)
                        logger.warn("Cannot change ip address for bot name: " + telegramBotModel.getName());
                }
            });
        }
    }
}
