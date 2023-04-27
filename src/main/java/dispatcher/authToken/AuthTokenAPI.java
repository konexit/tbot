package dispatcher.authToken;

import com.sun.net.httpserver.HttpExchange;
import dispatcher.authToken.service.AuthTokenService;

public class AuthTokenAPI {

    private static AuthTokenAPI authTokenAPI;
    private AuthTokenAPI() {}
    public static synchronized AuthTokenAPI getInstance() {
        if (authTokenAPI == null) authTokenAPI = new AuthTokenAPI();
        return authTokenAPI;
    }

    private AuthTokenService authTokenService = AuthTokenService.getInstance();

    public String getToken(String authTokenURL, String ckEditorCredentials){
        return authTokenService.getToken(authTokenURL, ckEditorCredentials);
    }

    public Boolean validateToken(HttpExchange httpExchange, String authTokenURL){
        return authTokenService.validateToken(httpExchange, authTokenURL);
    }
}