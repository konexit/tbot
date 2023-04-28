package dispatcher.telegramAPI.handlers;

import com.mashape.unirest.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dispatcher.http.HTTP;
import dispatcher.system.SystemAPI;
import dispatcher.telegramAPI.TelegramAPI;
import dispatcher.telegramAPI.models.ResponseJsonModel;
import dispatcher.telegramAPI.models.TelegramBotModel;
import dispatcher.unit.Converter;

import java.util.HashMap;

public class GetUpdatesHandler implements HttpHandler {

    private SystemAPI systemAPI = SystemAPI.getInstance();
    private HTTP http = HTTP.getInstance();
    private TelegramAPI telegramAPI = TelegramAPI.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        String botToken;

        try {
            botToken = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
        } catch (Exception e){
            http.createResponseWithLog(httpExchange, "Cannot get bot from parameter", 500,
                    "info", null);
            return;
        }

        TelegramBotModel telegramBotModelMap = telegramAPI.getTelegramBotConfig(botToken);
        if (telegramBotModelMap == null || !telegramBotModelMap.getState()) {
            http.createResponseWithLog(httpExchange, "info", 500,
                    "The server is under maintenance by BotToken: " + botToken, null);
            return;
        }

        String json = Converter.getBodyFromHttpExchange(httpExchange);
        HttpResponse httpResponse = http.postRequestWithToken(telegramBotModelMap.getServer(), json, systemAPI.getDispatcherAuthToken());

        if (httpResponse == null || httpResponse.getStatus() == 401) {
            systemAPI.refreshDispatcherToken();
            httpResponse = http.postRequestWithToken(telegramBotModelMap.getServer(), json, systemAPI.getDispatcherAuthToken());
        }

        if (httpResponse == null || httpResponse.getStatus() != 200){
            http.createResponseWithLog(httpExchange, "info", 500,
                    "Problem in service: " + telegramBotModelMap.getServer(), null);
            return;
        }

        ResponseJsonModel jsonModel = (ResponseJsonModel) Converter.convertStringToSpecificObject(httpResponse.getBody().toString(), ResponseJsonModel.class);
        if (jsonModel == null){
            http.createResponseWithLog(httpExchange, "warn", 500, "Cannot convert response json to jsonModel", null);
            return;
        }

        HashMap<String, Object> respMessage = telegramAPI.sendTelegramMessage(botToken, jsonModel);
        if ((Integer)respMessage.get("code") != 200) {
            http.createResponseWithLog(httpExchange, "info", 500,
                    respMessage.get("error").toString(), null);
            return;
        }

        http.createResponse(httpExchange, 200, "{\"status\":\"Success send to server \"}", null);
    }
}