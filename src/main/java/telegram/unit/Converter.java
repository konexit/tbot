package telegram.unit;

import com.sun.net.httpserver.HttpExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Converter {

    private static final Logger logger = LogManager.getLogger();

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
}
