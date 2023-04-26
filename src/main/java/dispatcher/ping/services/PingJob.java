package dispatcher.ping.services;

import com.mashape.unirest.http.HttpResponse;
import dispatcher.http.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

public class PingJob implements Job {

    private static final Logger logger = LogManager.getLogger();

    private HTTP http = HTTP.getInstance();

    @Override
    public void execute(JobExecutionContext context) {

        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

        String serverURL = (String) jobDataMap.get("serverURL");
        if (serverURL == null){
            logger.info("Cannot get serverURL: " + serverURL);
            return;
        }

        HttpResponse response = http.getRequest(serverURL);
        if (response == null || response.getStatus() != 200){
            logger.info("Problem with server: " + serverURL);
            return;
        }
    }
}
