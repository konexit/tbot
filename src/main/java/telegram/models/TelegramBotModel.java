package telegram.models;

import java.util.List;
import java.util.Map;

public class TelegramBotModel {

    private String server;
    private String name;
    private Boolean state;
    private List<Map<String, Object>> jobsConfig;

    public TelegramBotModel() {}

    public String getServer() {
        return server;
    }

    public String getName() {
        return name;
    }

    public Boolean getState() {
        return state;
    }

    public List<Map<String, Object>> getJobsConfig() {
        return jobsConfig;
    }
}
