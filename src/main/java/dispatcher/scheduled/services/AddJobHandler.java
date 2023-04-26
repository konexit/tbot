package dispatcher.scheduled.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dispatcher.http.HTTP;
import dispatcher.scheduled.models.JobConfigModel;
import dispatcher.unit.Converter;

import java.util.Map;

public class AddJobHandler implements HttpHandler {

    private HTTP http = HTTP.getInstance();
    private ScheduledService scheduledService = ScheduledService.getInstance();

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

        String jobGroup = (String) jsonMap.get("jobGroup");
        JobConfigModel jobConfigModel =
                (JobConfigModel) Converter.convertObjectToSpecificObject(jsonMap.get("jobsConfig"),
                        new TypeReference<JobConfigModel>(){});

        Map<String, Object> requestData =
                (Map) Converter.convertObjectToSpecificObject(jsonMap.get("requestData"),  new TypeReference<Map<String, Object>>(){});


        if (jobGroup == null || jobConfigModel == null || requestData == null){
            http.createResponseWithLog(httpExchange, "info", 500, "Problem with converting data to specific object", null);
            return;
        }

        Boolean status = scheduledService.addJob(jobGroup, jobConfigModel, requestData);
        if (!status) {
            http.createResponseWithLog(httpExchange, "info", 500, "Cannot add job to scheduler", null);
            return;
        }

        http.createResponse(httpExchange, 200, "{\"status\":\"Success added job\"}", null);
    }
}
