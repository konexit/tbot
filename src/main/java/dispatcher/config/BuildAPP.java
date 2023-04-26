package dispatcher.config;

import com.fasterxml.jackson.databind.JsonNode;
import dispatcher.ping.PingAPI;
import dispatcher.scheduled.ScheduledAPI;
import dispatcher.system.SystemAPI;
import dispatcher.telegramAPI.TelegramAPI;

import java.util.Properties;

public class BuildAPP {

    private static BuildAPP buildAPP;
    private BuildAPP() {}
    public static synchronized BuildAPP getInstance() {
        if (buildAPP == null) buildAPP = new BuildAPP();
        return buildAPP;
    }

    public ScheduledAPI scheduled = ScheduledAPI.getInstance();
    public SystemAPI systemAPI = SystemAPI.getInstance();
    private ApplicationProperties applicationProperties = ApplicationProperties.getInstance();
    private TelegramAPI telegramAPI = TelegramAPI.getInstance();
    private PingAPI pingAPI = PingAPI.getInstance();

    public Boolean init(){
        Properties properties = applicationProperties.getApplicationProperties();

        if (properties.size() == 0 || !systemAPI.setSystemApplicationProperties(properties)
                || !telegramAPI.setTelegramApplicationProperties(properties)) return false;

        JsonNode config = systemAPI.getConfigFile();
        if (config == null || !systemAPI.setSystemConfig(config.get("system"))
                || !telegramAPI.setTelegramConfig(config.get("telegram"))
                || !pingAPI.setPingConfig(config.get("ping"))) return false;

        if (!scheduled.run()) return false;
        systemAPI.addSystemJobs();
        telegramAPI.addTelegramJobs();
        pingAPI.addPingJobs();

        return true;
    }

}