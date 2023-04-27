package dispatcher.http;

import com.google.gson.Gson;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.JobDataMap;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HTTP {

    private static HTTP http;
    private HTTP() {}
    public static synchronized HTTP getInstance() {
        if (http == null) http = new HTTP();
        return http;
    }

    private static final Logger logger = LogManager.getLogger();

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
            logger.info("Error post request with token: " + serverURL + "  EXCEPTION:" + e.getMessage());
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
            logger.info("Error post request: " + serverURL + "  EXCEPTION:" + e.getMessage());
        }
        return response;
    }

    public HttpResponse getRequest(String serverURL) {
        HttpResponse response = null;
        try {
            response = Unirest.get(serverURL).asString();
        } catch (UnirestException e) {
            logger.info("Error get request: " + serverURL + "  EXCEPTION:" + e.getMessage());
        }
        return response;
    }

    public HttpResponse requestByJobDataMap(JobDataMap jobDataMap, String dispatcherAuthToken) {
        HttpResponse response = null;

        Map<String, String> headers = jobDataMap.get("headers") != null ? (Map<String, String>) jobDataMap.get("headers") : new HashMap<>();
        headers.put("Accept", "application/json");
        headers.put("Authorization", "Bearer " + dispatcherAuthToken);
        headers.put("Content-Type", "application/json");

        try {
            if (jobDataMap.get("method") != null && jobDataMap.get("method").equals("POST")) {
                String json = jobDataMap.get("json") != null ? new Gson().toJson(jobDataMap.get("json")).toString() : "{}";
                response = Unirest.post(jobDataMap.getString("serverURL")).headers(headers).body(json).asString();}
            else response = Unirest.get(jobDataMap.getString("serverURL")).headers(headers).asString();
        } catch (Exception e) {
            logger.warn("SchedulerRequest: EXCEPTION! " + e.getMessage());
        }

        return response;
    }

    public void createResponse(HttpExchange httpExchange, int statusCode, String json, Map<String, List<String>> headers) {
        try {
            byte[] body = json.getBytes();
            if (headers == null) httpExchange.getResponseHeaders().set("Content-Type", "application/json");
            else httpExchange.getResponseHeaders().putAll(headers);
            httpExchange.sendResponseHeaders(statusCode, body.length);
            OutputStream outputStream = httpExchange.getResponseBody();
            outputStream.write(body);
            outputStream.close();
        } catch (IOException e) {
            logger.warn("Cannot create response EXCEPTION: " + e.getMessage());
        }
    }

    public void createResponseWithLog(HttpExchange httpExchange, String level, int statusCode, String cause, Map<String, List<String>> headers) {
        switch (level) {
            case "info": logger.info(cause); break;
            case "warn": logger.warn(cause); break;
        }
        createResponse(httpExchange, statusCode, "{\"status\": \"failed\", \"error\": \"" + cause + "\"}", headers);
    }
}