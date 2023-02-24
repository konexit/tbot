package telegram.http;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.sun.net.httpserver.HttpExchange;
import telegram.GeneralData;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class HTTP {

    GeneralData generalData = GeneralData.getInstance();

    private static HTTP http;
    private HTTP() {}
    public static HTTP getInstance() {
        if (http == null) http = new HTTP();
        return http;
    }

    public HttpResponse sendServer(String serverURL, InputStream json) {
        HttpResponse response = null;
        try {
            response = Unirest.post(serverURL)
                    .header("Accept", "application/json")
                    .header("Authorization", "Basic " + generalData.getAccessToken())
                    .header("Content-Type", "application/json")
                    .body(json)
                    .asString();
        } catch (Exception e) {
            e.getStackTrace();
        }
        return response;
    }

    public HttpResponse sendMessage(String botToken, String json) {
        HttpResponse response = null;
        try {
            response = Unirest.post(generalData.getTelegramURL() + "/bot" + botToken + "/sendMessage")
                    .header("Accept", "application/json")
                    .header("Content-Type", "application/json")
                    .body(json)
                    .asString();
        } catch (Exception e) {
            System.out.println("request error " + e.getStackTrace());
        }
        return response;
    }

    public void createResponse(HttpExchange httpExchange, int code, String json) {
        try {
            httpExchange.sendResponseHeaders(code, json.length());
            OutputStream os = httpExchange.getResponseBody();
            os.write(json.getBytes());
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}