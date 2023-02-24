package telegram.handlers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import telegram.models.TelegramBotModel;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyHttpHandler implements HttpHandler {

    public void handle(HttpExchange httpExchange) throws IOException {
        Map<String, String> dbBots = new HashMap<>();
        dbBots.put("tester_meters_bot", "6034973591:AAH5qoOJ1TEfzi7m2iPXw3A_K8auh6FiNZM");
        dbBots.put("meters_bot_bot", "6067985401:AAEU8juQByqUsYPluZTy4diE99HempOQm_E");

        String testJson = "[\n" +
                "  {\n" +
                "    \"6067985401:AAEU8juQByqUsYPluZTy4diE99HempOQm_E\": {\n" +
                "      \"sevice\": \"https://meters.konex.com.ua/telegram\",\n" +
                "      \"state\": true\n" +
                "    }\n" +
                "  },\n" +
                "  {\n" +
                "    \"5446759992:AAG-huDYrefdwknaGBBpbliD0yRJbmUDQ_o\": {\n" +
                "      \"sevice\": \"https://meters.konex.com.ua/telegram\",\n" +
                "      \"state\": true\n" +
                "    }\n" +
                "  }\n" +
                "]";
        System.out.println(testJson);

        ArrayList<Map<String, TelegramBotModel>> retMap = new Gson().fromJson(testJson, new TypeToken<ArrayList<HashMap<String, TelegramBotModel>>>() {}.getType());



        OutputStream outputStream = httpExchange.getResponseBody();
        StringBuilder htmlBuilder = new StringBuilder();
        String requestBot = httpExchange.getRequestURI().toString().split("\\?")[1].split("=")[1];
        System.out.println(requestBot);
//        System.out.println(getJson(httpExchange));
        if (dbBots.get(requestBot).isEmpty()) return;
        sendMessage(dbBots.get(requestBot), "HELLO from: " + requestBot);
        htmlBuilder.append("test");
        String htmlResponse = htmlBuilder.toString();
        httpExchange.sendResponseHeaders(200, htmlResponse.length());
        outputStream.write(htmlResponse.getBytes());
        outputStream.flush();
        outputStream.close();
    }


    private void sendMessage(String botToken, String message) {
        URL url = null;
        try {
            url = new URL("https://api.telegram.org/bot" + botToken + "/sendMessage?chat_id=" + 971483502 + "&text=" + message);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Accept", "application/json");
            con.setDoOutput(false);
            System.out.println(con.getResponseCode());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private String getJson(HttpExchange exchange){
//        BufferedReader httpInput = null;
//        StringBuilder in = new StringBuilder();
//        try {
//            httpInput = new BufferedReader(new InputStreamReader(
//                    exchange.getRequestBody(), "UTF-8"));
//
//            String input;
//            while ((input = httpInput.readLine()) != null) {
//                in.append(input).append(" ");
//            }
//            httpInput.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return in.toString().trim();
//    }
}