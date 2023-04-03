package telegram.http;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.net.httpserver.HttpExchange;
import org.quartz.JobDataMap;
import telegram.config.GeneralData;
import telegram.models.JsonModel;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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

    public HttpResponse request(String serverURL, String method, String json) {
        HttpResponse response = null;
        try {
            response = Unirest.post(serverURL)
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + generalData.getTelegramURL())
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
        if (chat_id != null) {
            chat_id.forEach((String chatId) -> jsonModel.getTelegram().forEach(message -> {
                message.put("chat_id", chatId);
                HttpResponse httpResponse = http.postRequest(generalData.getTelegramURL() + "/bot" + botToken + "/sendMessage", new Gson().toJson(message));
                if (httpResponse == null || httpResponse.getStatus() != 200) {
                    resp.put("code", httpResponse == null ? 500 : httpResponse.getStatus());
                    resp.put("error", httpResponse == null ? "Problem with telegram" : httpResponse.getStatusText());
                }
            }));
        }
        if (!resp.containsKey("code")) resp.put("code", 200);
        return resp;
    }

    public HttpResponse schedulerRequest(JobDataMap jobDataMap) {
        HttpResponse response = null;

        Map<String, String> headers = jobDataMap.get("headers") != null ? (Map<String, String>) jobDataMap.get("headers") : new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + generalData.getAccessToken());
        headers.put("Content-Type", "application/json");

        try {
            if (jobDataMap.get("method") != null && jobDataMap.get("method").equals("POST")) {
                String json = jobDataMap.get("json") != null ? new Gson().toJson(jobDataMap.get("json")).toString() : "{}";
                response = Unirest.post(jobDataMap.getString("serverURL")).headers(headers).body(json).asString();}
            else response = Unirest.get(jobDataMap.getString("serverURL")).headers(headers).asString();
        } catch (UnirestException e) {
            System.out.println("SchedulerRequest:  EXCEPTION! " + e.getStackTrace()) ;
        }

        return response;
    }
}