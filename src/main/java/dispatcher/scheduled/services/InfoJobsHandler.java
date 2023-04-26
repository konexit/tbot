package dispatcher.scheduled.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dispatcher.http.HTTP;
import dispatcher.scheduled.ScheduledAPI;

public class InfoJobsHandler implements HttpHandler {

    private HTTP http = HTTP.getInstance();
    private ScheduledAPI scheduledAPI = ScheduledAPI.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        http.createResponse(httpExchange, 200, scheduledAPI.getInfoJobs(), null);
    }
}
