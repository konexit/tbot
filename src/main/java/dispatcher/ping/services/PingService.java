package dispatcher.ping.services;

import com.fasterxml.jackson.core.type.TypeReference;
import dispatcher.ping.config.PingConfig;
import dispatcher.scheduled.ScheduledAPI;
import dispatcher.scheduled.models.JobConfigModel;
import dispatcher.unit.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class PingService {

    private static PingService konexService;
    private PingService() {}
    public static synchronized PingService getInstance() {
        if (konexService == null) konexService = new PingService();
        return konexService;
    }

    private static final Logger logger = LogManager.getLogger();

    private PingConfig pingConfig = PingConfig.getInstance();
    private ScheduledAPI scheduledAPI = ScheduledAPI.getInstance();

    public void addPingJobs(){
        Map<String, Object> system = pingConfig.getMapPingConfigData();

        List<JobConfigModel> pingJobList =
                (List<JobConfigModel>) Converter.convertObjectToSpecificObject(system.get("jobsConfig"),
                        new TypeReference<List<JobConfigModel>>(){});

        if (pingJobList == null) {
            logger.warn("Cannot convert json ping job config to List");
            return;
        }

        if (pingJobList.size() > 0) {
            pingJobList.forEach(systemJobModel -> scheduledAPI.addJob("ping", systemJobModel, systemJobModel.getRequest()));
        }
    }

}
