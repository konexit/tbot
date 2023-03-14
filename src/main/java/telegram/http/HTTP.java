package telegram.http;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.net.httpserver.HttpExchange;
import telegram.handlers.LoggerHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

public class HTTP {

    private static HTTP http;
    private HTTP() {}
    public static HTTP getInstance() {
        if (http == null) http = new HTTP();
        return http;
    }

    private static final Logger logger = Logger.getLogger(LoggerHandler.class.getName());

    public HttpResponse postRequestWithToken(String serverURL, String json, String token) {
        HttpResponse response = null;
        try {
            response = Unirest.post(serverURL)
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .body(json)
                    .asString();
        } catch (UnirestException e) {
            logger.info("HTTP: EXCEPTION! sendServerToken()... " + e.getStackTrace());
        }
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
        } catch (UnirestException e) {
            logger.info("HTTP: EXCEPTION! sendServer()... " + e.getStackTrace());
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