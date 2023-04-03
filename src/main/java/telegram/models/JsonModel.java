package telegram.models;

import java.util.List;
import java.util.Map;

public class JsonModel {

    private List<Map<String, Object>> telegram;
    private Map<String, Object> telegramDispatcher;

    public List<Map<String, Object>> getTelegram() {
        return telegram;
    }

    public Map<String, Object> getTelegramDispatcher() {
        return telegramDispatcher;
    }

    public void setTelegram(List<Map<String, Object>> telegram) {
        this.telegram = telegram;
    }

}
