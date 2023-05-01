package dispatcher.scheduled.services;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import dispatcher.authToken.AuthTokenAPI;
import dispatcher.http.HTTP;
import dispatcher.scheduled.ScheduledAPI;
import dispatcher.system.config.SystemConfig;

public class InfoJobsHandler implements HttpHandler {

    private HTTP http = HTTP.getInstance();
    private ScheduledAPI scheduledAPI = ScheduledAPI.getInstance();
    private AuthTokenAPI authTokenAPI = AuthTokenAPI.getInstance();
    private SystemConfig systemConfig = SystemConfig.getInstance();

    @Override
    public void handle(HttpExchange httpExchange) {
        Boolean validToken = authTokenAPI.validateToken(httpExchange,systemConfig.getPropertiesByKey("authTokenURL") + "/publicKey");
        if (!validToken) {
            http.createResponse(httpExchange, 401, "{\"error\": \"JWT token is invalid\"}", null);
            return;
        }

        http.createResponse(httpExchange, 200, scheduledAPI.getInfoJobs(), null);
    }
}
