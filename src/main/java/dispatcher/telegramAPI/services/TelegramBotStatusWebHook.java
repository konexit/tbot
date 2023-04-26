package dispatcher.telegramAPI.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.mashape.unirest.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dispatcher.http.HTTP;
import dispatcher.telegramAPI.config.TelegramConfig;
import dispatcher.unit.Converter;

import java.util.Map;

public class TelegramBotStatusWebHook implements HttpHandler {

    private HTTP http = HTTP.getInstance();
    private TelegramConfig telegramConfig = TelegramConfig.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        String json = Converter.getBodyFromHttpExchange(httpExchange);

        if (json.isEmpty()) {
            http.createResponseWithLog(httpExchange, "info", 500, "body is empty", null);
            return;
        }

        Map<String, Object> jsonMap = (Map) Converter.convertStringToSpecificObject(json, Map.class);
        if (jsonMap == null) {
            http.createResponseWithLog(httpExchange, "info", 500, "cannot parse json to map", null);
            return;
        }

        String botName = (String) jsonMap.get("botName");
        if (botName == null) {
            http.createResponseWithLog(httpExchange, "info", 500, "cannot find key botName", null);
            return;
        }

        String botToken = telegramConfig.getBotTokenByName(botName);
        if (botToken == null) {
            http.createResponseWithLog(httpExchange, "info", 500, "not found bot", null);
            return;
        }

        HttpResponse httpResponse = http.getRequest( telegramConfig.getPropertiesByKey("telegramURL") +
                                                                "/bot" + botToken + "/getWebhookInfo");
        if (httpResponse == null || httpResponse.getStatus() != 200) {
            http.createResponseWithLog(httpExchange, "info", 500, httpResponse.getStatusText(), null);
            return;
        }

        JsonNode webHookJson = (JsonNode) Converter.convertStringToSpecificObject(httpResponse.getBody().toString(), JsonNode.class);
        Boolean botStatus = webHookJson.get("ok").asBoolean();
        if (!botStatus) {
            http.createResponseWithLog(httpExchange, "info", 500, webHookJson.get("description").asText(), null);
        }

        http.createResponse(httpExchange, 200, "{\"status\": " + webHookJson.get("ok") + ", " +
                "\"pending_update_count\": " + webHookJson.get("result").get("pending_update_count") + "}", null);
    }
}
