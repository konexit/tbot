package dispatcher.ping.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import dispatcher.unit.Converter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class PingConfig {

    private static PingConfig pingConfig;
    private PingConfig() {}
    public static synchronized PingConfig getInstance() {
        if (pingConfig == null) pingConfig = new PingConfig();
        return pingConfig;
    }

    private static final Logger logger = LogManager.getLogger();

    private Map<String, Object> mapPingConfig = new HashMap<>();

    public Boolean setPingConfig(JsonNode jsonPingConfig) {
        if (jsonPingConfig == null) {
            logger.warn("Cannot find key ping in config file");
            return false;
        }

        HashMap<String, Object> systemConfig =
                (HashMap<String, Object>) Converter.convertObjectToSpecificObject(jsonPingConfig,
                        new TypeReference<HashMap<String, Object>>() {});

        if (systemConfig == null) {
            logger.warn("Cannot convert json ping config to HashMap");
            return false;
        }

        mapPingConfig = systemConfig;
        return true;
    }

    public Map<String, Object> getMapPingConfigData() {
        return mapPingConfig;
    }
}
