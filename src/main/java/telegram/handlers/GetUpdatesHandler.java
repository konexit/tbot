package telegram.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import telegram.config.GeneralData;
import telegram.http.HTTP;
import telegram.models.TelegramBotModel;

import java.io.ByteArrayInputStream;

public class GetUpdatesHandler implements HttpHandler {

    private GeneralData generalData = GeneralData.getInstance();
    private HTTP http = HTTP.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        String botToken = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
        TelegramBotModel telegramBotModelMap = generalData.getListTelegramBot(botToken);
        if (telegramBotModelMap == null || !telegramBotModelMap.getState()) http.createResponse(httpExchange, 200, "{\"text\":\"The server is under maintenance\"}");
        else http.postRequestWithToken(telegramBotModelMap.getServer(), httpExchange.getRequestBody(), generalData.getAccessToken());
    }
}