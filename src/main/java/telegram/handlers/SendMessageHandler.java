package telegram.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import telegram.config.GeneralData;
import telegram.http.HTTP;
import telegram.models.JsonModel;
import telegram.models.TelegramBotModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SendMessageHandler implements HttpHandler {

    private static final Logger logger = Logger.getLogger(LoggerHandler.class.getName());
    private GeneralData generalData = GeneralData.getInstance();
    private HTTP http = HTTP.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        JsonModel jsonModel;
        try {
            jsonModel = new Gson().fromJson(getBodyFromHttpExchange(httpExchange), new TypeToken<JsonModel>() {}.getType());
            if (jsonModel == null) throw new Exception();
        } catch (Exception e){
            logger.info("Cannot convert json to model");
            http.createResponse(httpExchange, 401, "{\"text\":\"Cannot convert json to model\"}");
            return;
        }
        TelegramBotModel telegramBotModelMap = generalData.getListTelegramBot(jsonModel.getBot());
        if (telegramBotModelMap == null || !telegramBotModelMap.getState()) http.createResponse(httpExchange, 200, "{\"text\":\"The server is under maintenance\"}");
        else sendMessage(httpExchange, jsonModel.getBot(), jsonModel);
        return;
    }

    private String getBodyFromHttpExchange(HttpExchange httpExchange) throws IOException {
        BufferedReader httpInput = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), "UTF-8"));
        String input;
        StringBuilder in = new StringBuilder();
        while ((input = httpInput.readLine()) != null) {
            in.append(input).append(" ");
        }
        httpInput.close();
        return in.toString().trim();
    }

    private void sendMessage(HttpExchange httpExchange, String botToken, JsonModel jsonModel) {
        try {
            ArrayList<String> chat_id = (ArrayList<String>) jsonModel.getTelegramDispatcher().get("chat_id");
            chat_id.forEach(chatId -> {
                jsonModel.setTelegramValue("chat_id", chatId);
                http.postRequest(generalData.getTelegramURL() + "/bot" + botToken + "/sendMessage", new Gson().toJson(jsonModel.getTelegram()));
            });
            http.createResponse(httpExchange, 200, "{\"text\":\"Successfully sent messages\"}");
        } catch (Exception e){
            logger.info("Json parse error");
            http.createResponse(httpExchange, 200, "{\"text\":\"Json parse error\"}");
        }
    }
}