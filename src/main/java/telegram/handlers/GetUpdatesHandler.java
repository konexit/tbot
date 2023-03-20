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
//import java.util.logging.Logger;

public class GetUpdatesHandler implements HttpHandler {

    private GeneralData generalData = GeneralData.getInstance();
    private HTTP http = HTTP.getInstance();

    private static final Logger logger = LogManager.getLogger();

    @Override
    public void handle(HttpExchange httpExchange) {
        String botToken;
        try {
            botToken = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
            for (int i = 0; i < 1000; i++) {
                logger.error(i + " Test error");
                logger.info(i + " Test info");
                logger.debug(i + " Test debug");
            }
        } catch (Exception e){
            responseFails(httpExchange, "Cannot get bot from parameter");
            return;
        }

        TelegramBotModel telegramBotModelMap = generalData.getListTelegramBot(botToken);
        if (telegramBotModelMap == null || !telegramBotModelMap.getState()) {
            responseFails(httpExchange, "The server is under maintenance");
            return;
        }

        HttpResponse httpResponse = http.postRequestWithToken(telegramBotModelMap.getServer(), Converter.getBodyFromHttpExchange(httpExchange), generalData.getAccessToken());
        if (httpResponse == null || httpResponse.getStatus() != 200) {
            responseFails(httpExchange, "Problem in service");
            return;
        }

        JsonModel jsonModel;
        try {
            jsonModel = new Gson().fromJson(httpResponse.getBody().toString(), new TypeToken<JsonModel>() {}.getType());
        } catch (JsonSyntaxException e) {
            responseFails(httpExchange,  e.getMessage());
            return;
        }

        HashMap<String, Object> respMessage = http.sendMessage(botToken, jsonModel);
        if ((Integer)respMessage.get("code") != 200) {
            responseFails(httpExchange, respMessage.get("error").toString());
            return;
        }

        http.createResponse(httpExchange, 200, "{\"status\":\"Success send to server \"}");
    }

    private void responseFails(HttpExchange httpExchange, String cause){
//        logger.info(cause);
        http.createResponse(httpExchange, 500, "{\"status\": \"failed\", \"error\":" + cause + "}");
    }
}