package dispatcher.swagger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dispatcher.http.HTTP;
import dispatcher.unit.Converter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SwaggerHandler implements HttpHandler {

    private HTTP http = HTTP.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        String swaggerPage = Converter.getTextFromFileInProjectDir("swaggerResources/swagger.html");

        Map<String, List<String>> headers = new HashMap<>();
        headers.put("Content-Type", new LinkedList<String>() {{ add("text/html; charset=utf-8"); } });
        http.createResponse(httpExchange, 200, swaggerPage, headers);
    }
}
