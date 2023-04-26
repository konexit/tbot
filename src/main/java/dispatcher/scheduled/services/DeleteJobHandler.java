package dispatcher.scheduled.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dispatcher.http.HTTP;
import dispatcher.scheduled.ScheduledAPI;
import dispatcher.unit.Converter;

import java.util.Map;

public class DeleteJobHandler implements HttpHandler {

    private HTTP http = HTTP.getInstance();
    private ScheduledAPI scheduledAPI = ScheduledAPI.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        String json = Converter.getBodyFromHttpExchange(httpExchange);

        if (json.isEmpty()) {
            http.createResponseWithLog(httpExchange, "info", 500, "Body is empty", null);
            return;
        }

        Map<String, Object> jsonMap = (Map) Converter.convertStringToSpecificObject(json, Map.class);
        if (jsonMap == null) {
            http.createResponseWithLog(httpExchange, "info", 500, "Cannot parse json to map", null);
            return;
        }

        String groupName = (String) jsonMap.get("groupName");
        String jobName = (String) jsonMap.get("jobName");

        if (groupName == null || jobName == null){
            http.createResponseWithLog(httpExchange, "info", 500, "Problem with converting data to specific object", null);
            return;
        }

        Boolean status = scheduledAPI.deleteJob(groupName, jobName);
        if (!status) {
            http.createResponseWithLog(httpExchange, "info", 500, "Cannot delete job to scheduler", null);
            return;
        }

        http.createResponse(httpExchange, 200, "{\"status\":\"Success deleted job\"}", null);
    }
}
