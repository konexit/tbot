package dispatcher.system.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import dispatcher.unit.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SystemConfig {

    private static SystemConfig systemConfig;

    private SystemConfig() {
    }

    public static synchronized SystemConfig getInstance() {
        if (systemConfig == null) systemConfig = new SystemConfig();
        return systemConfig;
    }

    private static final Logger logger = LogManager.getLogger();

    private String dispatcherAuthToken = "";
    private String activeCommunicationChannel = "";
    private Map<String, Object> mapSystemConfig = new HashMap<>();
    private Map<String, Object> systemApplicationProperties = new HashMap<>();

    public Boolean setSystemConfig(JsonNode jsonSystemConfig) {
        if (jsonSystemConfig == null) {
            logger.warn("Cannot find key system in config file");
            return false;
        }

        HashMap<String, Object> systemConfig =
                (HashMap<String, Object>) Converter.convertObjectToSpecificObject(jsonSystemConfig,
                        new TypeReference<HashMap<String, Object>>() {});

        if (systemConfig == null) {
            logger.warn("Cannot convert json system config to HashMap");
            return false;
        }

        mapSystemConfig = systemConfig;
        return true;
    }

    public Boolean setSystemApplicationProperties(Properties prop) {
        try {
            systemApplicationProperties.put("domain", prop.getProperty("domain"));
            systemApplicationProperties.put("serverPort", Integer.parseInt(prop.getProperty("server.port")));
            systemApplicationProperties.put("serverThreadCount", Integer.parseInt(prop.getProperty("server.threadCount")));
            systemApplicationProperties.put("authTokenURL", prop.getProperty("AuthToken.URL"));
            systemApplicationProperties.put("ckEditorCredentials", prop.getProperty("ckEditor.Credentials"));
            return true;
        } catch (NumberFormatException e) {
            logger.warn("Cannot convert system property to some object EXCEPTION! " + e.getMessage());
            return false;
        }

    }

    public Object getPropertiesByKey(String key) {
        return systemApplicationProperties.get(key);
    }

    public String getDispatcherAuthToken() {
        return dispatcherAuthToken;
    }

    public void setDispatcherAuthToken(String dispatcherAuthToken) {
        this.dispatcherAuthToken = dispatcherAuthToken;
    }

    public String getActiveCommunicationChannel() {
        return activeCommunicationChannel;
    }

    public void setActiveCommunicationChannel(String activeCommunicationChannel) {
        this.activeCommunicationChannel = activeCommunicationChannel;
    }

    public Map<String, Object> getMapSystemConfigData() {
        return mapSystemConfig;
    }
}
