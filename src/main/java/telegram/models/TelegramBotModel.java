package telegram.models;

import java.util.List;
import java.util.Map;

public class TelegramBotModel {

    private String server;
    private String name;
    private Boolean state;
    private List<ScheduledJobModel> jobsConfig;

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

    public List<ScheduledJobModel> getJobsConfig() {
        return jobsConfig;
    }
}
