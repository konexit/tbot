package dispatcher.system;

import com.fasterxml.jackson.databind.JsonNode;
import dispatcher.system.config.SystemConfig;
import dispatcher.system.services.SystemService;

import java.util.Properties;

public class SystemAPI {

    private static SystemAPI systemAPI;
    private SystemAPI() {}
    public static synchronized SystemAPI getInstance() {
        if (systemAPI == null) systemAPI = new SystemAPI();
        return systemAPI;
    }

    private SystemConfig systemConfig = SystemConfig.getInstance();
    private SystemService systemService = SystemService.getInstance();

    public void addSystemJobs(){
        systemService.addSystemJobs();
    }

    public void refreshDispatcherToken(){
        systemService.refreshDispatcherToken();
    }

    public JsonNode getConfigFile(){
        return systemService.getConfigFile();
    }

    public Boolean setSystemConfig(JsonNode jsonSystemConfig){
        return systemConfig.setSystemConfig(jsonSystemConfig);
    }

    public String getDispatcherAuthToken() {
        return systemConfig.getDispatcherAuthToken();
    }

    public Boolean setSystemApplicationProperties(Properties properties){
        return systemConfig.setSystemApplicationProperties(properties);
    }

    public Object getPropertiesByKey(String key) {
        return systemConfig.getPropertiesByKey(key);
    }

    public String getActiveCommunicationChannel(){
        return systemConfig.getActiveCommunicationChannel();
    }

    public void setActiveCommunicationChannel(String activeCommunicationChannel) {
        systemConfig.setActiveCommunicationChannel(activeCommunicationChannel);
    }
}
