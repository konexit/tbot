package dispatcher.telegramAPI.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dispatcher.authToken.AuthTokenAPI;
import dispatcher.http.HTTP;
import dispatcher.system.config.SystemConfig;
import dispatcher.telegramAPI.TelegramAPI;
import dispatcher.telegramAPI.config.TelegramConfig;
import dispatcher.telegramAPI.models.ResponseJsonModel;
import dispatcher.unit.Converter;

import java.util.HashMap;

public class SendMessageHandler implements HttpHandler {

    private HTTP http = HTTP.getInstance();
    private AuthTokenAPI authTokenAPI = AuthTokenAPI.getInstance();
    private SystemConfig systemConfig = SystemConfig.getInstance();
    private TelegramConfig telegramConfig = TelegramConfig.getInstance();
    private TelegramAPI telegramAPI = TelegramAPI.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        Boolean validToken = authTokenAPI.validateToken(httpExchange,systemConfig.getPropertiesByKey("authTokenURL") + "/publicKey");
        if (!validToken) {
            http.createResponse(httpExchange, 401, "{\"error\": \"JWT token is invalid\"}", null);
            return;
        }

        String body = Converter.getBodyFromHttpExchange(httpExchange);
        if (body == null) {
            http.createResponse(httpExchange, 401, "{\"text\":\"Body is empty\"}", null);
            return;
        }

        ResponseJsonModel responseJsonModel = (ResponseJsonModel) Converter.convertStringToSpecificObject(body, ResponseJsonModel.class);
        if (responseJsonModel == null){
            http.createResponse(httpExchange, 500, "{\"text\":\"Cannot convert json to model\"}", null);
            return;
        }

        String botName = (String) responseJsonModel.getTelegramDispatcher().get("botName");
        if (botName == null) {
            http.createResponse(httpExchange, 500, "{\"text\":\"Key of botName not found\"}", null);
            return;
        }

        String botToken = telegramConfig.getTokenByBotName(botName);
        if (botToken == null) {
            http.createResponseWithLog(httpExchange, "info", 500, "not found bot", null);
            return;
        }

        HashMap<String, Object> respMessage = telegramAPI.sendTelegramMessage(botToken, responseJsonModel);
        if ((Integer)respMessage.get("code") != 200) {
            http.createResponseWithLog(httpExchange, "info", 500,
                    respMessage.get("error").toString(), null);
            return;
        }

        http.createResponse(httpExchange, 200, "{\"status\":\"Success send to server \"}", null);
    }
}