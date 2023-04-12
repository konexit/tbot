package telegram.handlers;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import telegram.config.GeneralData;
import telegram.http.HTTP;
import telegram.models.JsonModel;
import telegram.models.TelegramBotModel;
import telegram.unit.Converter;

import java.util.HashMap;

public class GetUpdatesHandler implements HttpHandler {

    private static final Logger logger = LogManager.getLogger();
    private GeneralData generalData = GeneralData.getInstance();
    private HTTP http = HTTP.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        String botToken;

        try {
            botToken = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
        } catch (Exception e){
            responseFails(httpExchange, "Cannot get bot from parameter", "info");
            return;
        }

        TelegramBotModel telegramBotModelMap = generalData.getTelegramBot(botToken);
        if (telegramBotModelMap == null || !telegramBotModelMap.getState()) {
            responseFails(httpExchange, "info", "The server is under maintenance by BotToken: " + botToken);
            return;
        }

        String json = Converter.getBodyFromHttpExchange(httpExchange);
        HttpResponse httpResponse = http.postRequestWithToken(telegramBotModelMap.getServer(), json, generalData.getAccessToken());

        if (httpResponse == null || httpResponse.getStatus() == 401) {
            generalData.refreshToken();
            httpResponse = http.postRequestWithToken(telegramBotModelMap.getServer(), json, generalData.getAccessToken());
        }

        if (httpResponse == null || httpResponse.getStatus() != 200){
            responseFails(httpExchange, "info", "Problem in service: " + telegramBotModelMap.getServer());
            return;
        }

        JsonModel jsonModel;
        try {
            jsonModel = new Gson().fromJson(httpResponse.getBody().toString(), new TypeToken<JsonModel>() {}.getType());
        } catch (JsonSyntaxException e) {
            responseFails(httpExchange, "warn", "Cannot convert response json to jsonModel EXCEPTION: " + e.getMessage());
            return;
        }

        HashMap<String, Object> respMessage = http.sendMessage(botToken, jsonModel);
        if ((Integer)respMessage.get("code") != 200) {
            responseFails(httpExchange, "info", respMessage.get("error").toString());
            return;
        }

        http.createResponse(httpExchange, 200, "{\"status\":\"Success send to server \"}");
    }

    private void responseFails(HttpExchange httpExchange, String level, String cause) {
        switch (level) {
            case "info": logger.info(cause);
            case "warn": logger.warn(cause);
        }
        http.createResponse(httpExchange, 500, "{\"status\": \"failed\", \"error\":" + cause + "}");
    }
}