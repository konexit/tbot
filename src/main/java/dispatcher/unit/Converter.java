package dispatcher.unit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Converter {

    private static final Logger logger = LogManager.getLogger();
    private static ObjectMapper mapper = new ObjectMapper();

    public static String getBodyFromHttpExchange(HttpExchange httpExchange) {
        StringBuilder in = new StringBuilder();
        try {
            BufferedReader httpInput = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), "UTF-8"));
            String input;
            while ((input = httpInput.readLine()) != null) {
                in.append(input).append(" ");
            }
            httpInput.close();
        } catch (IOException e) {
            logger.warn("Cannot get json from httpExchange EXCEPTION: " + e.getMessage());
        }
        return in.toString().trim();
    }

    public static String getTextFromFileInProjectDir(String filePath){
        String fileContent = "";
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(System.getProperty("user.dir") + File.separator  + filePath));
            fileContent = new String (bytes);
        } catch (Exception e) {
            logger.fatal("Cannot read file");
        }
        return fileContent;
    }

    public static Object convertObjectToSpecificObject(Object object, TypeReference typeReference){
        try {
            return mapper.convertValue(object, typeReference);
        } catch (IllegalArgumentException e) {
            logger.warn("Cannot convert object to specific object");
            return null;
        }
    }

    public static Object convertStringToSpecificObject(String json, Class valueType){
        try {
            return mapper.readValue(json, valueType);
        } catch (JsonProcessingException e) {
            logger.warn("Cannot convert string to json node");
            return null;
        }
    }

    public static String convertObjectToJson(Object object){
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.warn("Cannot convert object to json");
            return null;
        }
    }
}
