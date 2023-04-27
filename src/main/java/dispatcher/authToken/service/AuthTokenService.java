package dispatcher.authToken.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mashape.unirest.http.HttpResponse;
import com.sun.net.httpserver.HttpExchange;
import dispatcher.http.HTTP;
import dispatcher.unit.Converter;
import io.jsonwebtoken.Jwts;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
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
        HttpResponse response = http.postRequest(authTokenURL, ckEditorCredentials);

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

    public Boolean validateToken(HttpExchange httpExchange, String authTokenURL){
        String token = getTokenFromRequest(httpExchange);
        if (token == null) return false;

        HttpResponse response = http.getRequest(authTokenURL);
        if (response == null || response.getStatus() != 200) return false;

        Map<String, Object> authResponse =
                (Map<String, Object>) Converter.convertStringToSpecificObject(response.getBody().toString(), Map.class);

        if (authResponse == null || authResponse.get("publicKey") == null) return false;

        try {
            Jwts.parser().setSigningKey(getPublicKeyBytes((String) authResponse.get("publicKey"))).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.info("Token is not valid");
            return false;
        }
    }

    private String getTokenFromRequest(HttpExchange httpExchange){
        String bearerToken = httpExchange.getRequestHeaders().getFirst("Authorization");
        if (bearerToken == null) return null;
        if (bearerToken.startsWith("Bearer ")) return bearerToken.substring(7, bearerToken.length());
        return bearerToken;
    }

    private PublicKey getPublicKeyBytes(String key) {
        X509EncodedKeySpec publicKey = new X509EncodedKeySpec(Base64.getDecoder().decode(key
                .replace("-----BEGIN RSA PUBLIC KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END RSA PUBLIC KEY-----", "")
                .replaceAll("\n", "")));

        try {
            return KeyFactory.getInstance("RSA").generatePublic(publicKey);
        } catch (GeneralSecurityException e) {
            logger.info("Problem to get public key EXCEPTION: " + e.getMessage());
            return null;
        }
    }
}
