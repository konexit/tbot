package telegram.http;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.net.httpserver.HttpExchange;
import telegram.config.GeneralData;
import telegram.models.JsonModel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class HTTP {

    private GeneralData generalData = GeneralData.getInstance();

    private static HTTP http;
    private HTTP() {}
    public static HTTP getInstance() {
        if (http == null) http = new HTTP();
        return http;
    }

    public HttpResponse postRequestWithToken(String serverURL, String json, String token) {
        HttpResponse response = null;
        try {
            response = Unirest.post(serverURL)
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(json)
                    .asString();
        } catch (UnirestException e) {}
        return response;
    }

    public HttpResponse postRequest(String serverURL, String json) {
        HttpResponse response = null;
        try {
            response = Unirest.post(serverURL)
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(json)
                    .asString();
        } catch (UnirestException e) {}
        return response;
    }

    public void createResponse(HttpExchange httpExchange, int statusCode, String json) {
        try {
            byte[] body = json.getBytes();
            httpExchange.sendResponseHeaders(statusCode, body.length);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(body);
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Object> sendMessage(String botToken, JsonModel jsonModel) {
        HashMap<String, Object> resp = new HashMap<>();
        if (!(Boolean) jsonModel.getTelegramDispatcher().get("sendMessage")) {
            resp.put("code", 200);
            return resp;
        }
        ArrayList<String> chat_id = (ArrayList<String>) jsonModel.getTelegramDispatcher().get("chat_id");
        chat_id.forEach((String chatId) -> jsonModel.getTelegram().forEach(message -> {
            message.put("chat_id", chatId);
            HttpResponse httpResponse = http.postRequest(generalData.getTelegramURL() + "/bot" + botToken + "/sendMessage", new Gson().toJson(message));
            if (httpResponse == null || httpResponse.getStatus() != 200) {
                resp.put("code", httpResponse == null ? 500 : httpResponse.getStatus());
                resp.put("error", httpResponse == null ? "Problem with telegram" : httpResponse.getStatusText());
            }
        }));
        if (!resp.containsKey("code")) resp.put("code", 200);
        return resp;
    }
}