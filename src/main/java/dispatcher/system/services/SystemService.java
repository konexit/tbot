package dispatcher.system.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import dispatcher.authToken.AuthTokenAPI;
import dispatcher.scheduled.ScheduledAPI;
import dispatcher.scheduled.models.JobConfigModel;
import dispatcher.system.config.SystemConfig;
import dispatcher.unit.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class SystemService {

    private static SystemService systemService;
    private SystemService() {}
    public static synchronized SystemService getInstance() {
        if (systemService == null) systemService = new SystemService();
        return systemService;
    }

    private static final Logger logger = LogManager.getLogger();

    private ScheduledAPI scheduledAPI = ScheduledAPI.getInstance();
    private AuthTokenAPI authTokenAPI = AuthTokenAPI.getInstance();
    private SystemConfig systemConfig = SystemConfig.getInstance();

    public void addSystemJobs(){
        Map<String, Object> system = systemConfig.getMapSystemConfigData();

        List<JobConfigModel> systemJobList =
                (List<JobConfigModel>) Converter.convertObjectToSpecificObject(system.get("jobsConfig"),
                        new TypeReference<List<JobConfigModel>>(){});

        if (systemJobList == null) {
            logger.warn("Cannot convert json system job config to List");
            return;
        }

        if (systemJobList.size() > 0) {
            systemJobList.forEach(systemJobModel -> scheduledAPI.addJob("system", systemJobModel, systemJobModel.getRequest()));
        }
    }

    public String refreshDispatcherToken(){
        String token = authTokenAPI.getToken((String) systemConfig.getPropertiesByKey("authTokenURL"), (String) systemConfig.getPropertiesByKey("ckEditor.Credentials"));
        if (token != null)systemConfig.setDispatcherAuthToken(token);
        return token;
    }

    public JsonNode getConfigFile(){
        JsonNode config = (JsonNode) Converter.convertStringToSpecificObject(Converter.getTextFromFileInProjectDir("config.json"), JsonNode.class);
        if (config == null) logger.warn("Cannot convert json config file to jsonNode");
        return config;
    }

}
