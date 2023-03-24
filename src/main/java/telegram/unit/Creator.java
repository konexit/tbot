//package telegram.unit;
//
//import telegram.models.JsonModel;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//
//public class Creator {
//
//    public static JsonModel jsonModel(String textMessage, List<String> chatIds) {
//        JsonModel jsonModel = new JsonModel();
//        jsonModel.setTelegram(Arrays.asList(new HashMap<String, Object>() {{ put("text", textMessage); put("parse_mode", "HTML"); }}));
//        jsonModel.setTelegramDispatcher(Collections.singletonMap("chat_id", chatIds));
//        return jsonModel;
//    }
//}
