package dispatcher.telegramAPI.models;

import dispatcher.scheduled.models.JobConfigModel;

import java.util.List;

public class TelegramBotModel {

    private String server;
    private String name;
    private Boolean state;
    private List<JobConfigModel> jobsConfig;

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

    public List<JobConfigModel> getJobsConfig() {
        return jobsConfig;
    }
}
