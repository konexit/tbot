package telegram.unit;

import com.sun.net.httpserver.HttpExchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Converter {

    public static String getBodyFromHttpExchange(HttpExchange httpExchange) {
        StringBuilder in = new StringBuilder();
        try {
            BufferedReader httpInput = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody(), "UTF-8"));
            String input;
            while ((input = httpInput.readLine()) != null) {
                in.append(input).append(" ");
            }
            httpInput.close();
        } catch (IOException e){

        }
        return in.toString().trim();
    }
}
