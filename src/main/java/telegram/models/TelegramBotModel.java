package telegram.models;

public class TelegramBotModel {

    private String server;
    private String name;
    private Boolean state;

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
}
