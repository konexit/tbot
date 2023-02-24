package telegram.models;

import java.util.Map;

public class JsonModel {

    private String bot;
    private Map<String, Object> telegram;
    private Map<String, Object> telegramDispatcher;

    public String getBot() {
        return bot;
    }

    public Map<String, Object> getTelegram() {
        return telegram;
    }

    public void setTelegramValue(String key, String value) {
        this.telegram.put(key, value);
    }

    public Map<String, Object> getTelegramDispatcher() {
        return telegramDispatcher;
    }
}
