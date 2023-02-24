package telegram.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import telegram.GeneralData;
import telegram.http.HTTP;
import telegram.models.TelegramBotModel;

import java.io.IOException;
import java.io.OutputStream;

public class GetUpdatesHandler implements HttpHandler {

    GeneralData generalData = GeneralData.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        String botToken = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
        TelegramBotModel telegramBotModelMap = generalData.getListTelegramBot(botToken);
        HTTP http = HTTP.getInstance();
        if (telegramBotModelMap == null || !telegramBotModelMap.getState()){
            System.out.println("Try later");
            sendMessage(httpExchange, "Try later");
        } else {
            http.sendServer(telegramBotModelMap.getServer(), httpExchange.getRequestBody());
        }
    }

    private void sendMessage(HttpExchange httpExchange, String text){
        OutputStream outputStream = httpExchange.getResponseBody();
        StringBuilder htmlBuilder = new StringBuilder();
        String requestBot = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
        System.out.println(requestBot);
        htmlBuilder.append(text);
        String htmlResponse = htmlBuilder.toString();
        try {
            httpExchange.sendResponseHeaders(200, htmlResponse.length());
            outputStream.write(htmlResponse.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}