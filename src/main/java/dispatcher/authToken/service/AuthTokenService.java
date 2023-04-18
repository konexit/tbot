package dispatcher.authToken.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import dispatcher.http.HTTP;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class AuthTokenService {

    private static AuthTokenService authTokenService;
    private AuthTokenService() {}
    public static synchronized AuthTokenService getInstance() {
        if (authTokenService == null) authTokenService = new AuthTokenService();
        return authTokenService;
    }

    private static final Logger logger = LogManager.getLogger();
    private HTTP http = HTTP.getInstance();

    public String getToken(String authTokenURL, String ckEditorCredentials){
        HttpResponse response = http.postRequest(authTokenURL + "/login", ckEditorCredentials);

        if (response == null || response.getStatus() != 200) {
            logger.info("Problem with authorization service");
        }

        try {
            Map<String, String> body = new Gson().fromJson(response.getBody().toString(), new TypeToken<HashMap<String, String>>() {}.getType());
            return body.get("access_token");
        } catch (Exception e){
            logger.error("Problem with json from authorization service");
            return null;
        }
    }
}
