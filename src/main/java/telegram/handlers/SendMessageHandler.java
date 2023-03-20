//package telegram.handlers;
//
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.sun.net.httpserver.HttpExchange;
//import com.sun.net.httpserver.HttpHandler;
//import telegram.config.GeneralData;
//import telegram.http.HTTP;
//import telegram.models.JsonModel;
//import telegram.models.TelegramBotModel;
//import telegram.unit.Converter;
//
//import java.util.HashMap;
//
//public class SendMessageHandler implements HttpHandler {
//
//    private GeneralData generalData = GeneralData.getInstance();
//    private HTTP http = HTTP.getInstance();
//
//    @Override
//    public void handle(HttpExchange httpExchange) {
//        JsonModel jsonModel;
//        try {
//            jsonModel = new Gson().fromJson(Converter.getBodyFromHttpExchange(httpExchange), new TypeToken<JsonModel>() {}.getType());
//            if (jsonModel == null) throw new Exception();
//        } catch (Exception e){
//            http.createResponse(httpExchange, 401, "{\"text\":\"Cannot convert json to model\"}");
//            return;
//        }
////        TelegramBotModel telegramBotModelMap = generalData.getListTelegramBot(jsonModel.getBot());
////        if (telegramBotModelMap == null || !telegramBotModelMap.getState()) http.createResponse(httpExchange, 200, "{\"text\":\"The server is under maintenance\"}");
////        else {
//            HashMap<String, Object> respMessage = http.sendMessage(jsonModel.getBot(), jsonModel);
//            if ((int)respMessage.get("code") == 200){
//                http.createResponse(httpExchange, 200, "{\"text\":\"Success send to server \"}");
//            } else {
//                System.out.println("Problem with telegram");
//                http.createResponse(httpExchange, 200, "{\"text\":\"Json parse error\"}");
//            }
////        }
//        return;
//    }
//}