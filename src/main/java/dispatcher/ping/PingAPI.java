package dispatcher.ping;


import com.fasterxml.jackson.databind.JsonNode;
import dispatcher.ping.config.PingConfig;
import dispatcher.ping.services.PingService;

public class PingAPI {

    private static PingAPI pingAPI;
    private PingAPI() {}
    public static synchronized PingAPI getInstance() {
        if (pingAPI == null) pingAPI = new PingAPI();
        return pingAPI;
    }

    private PingConfig pingConfig = PingConfig.getInstance();
    private PingService pingService = PingService.getInstance();

    public void addPingJobs(){
        pingService.addPingJobs();
    }

    public Boolean setPingConfig(JsonNode jsonPingConfig){
        return pingConfig.setPingConfig(jsonPingConfig);
    }

}
