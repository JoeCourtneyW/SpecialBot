package modules.StatTracking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import utils.http.ApiRequest;

import java.time.Instant;
import java.util.Base64;

public class Auth {
    private static final String RAINBOW_SIX_APPID = "39baebad-39e5-4552-8c25-2c9b919064e2";
    private String key;
    private String sessionId;
    private Instant expiration;

    private Auth(String key, String sessionId, Instant expiration) {
        this.key = key;
        this.sessionId = sessionId;
        this.expiration = expiration;
    }

    public ApiRequest authorizedRequest(String url){
        return new ApiRequest(url).addHeader("Authorization", "Ubi_v1 t=" + key)
                .addHeader("Ubi-AppId", RAINBOW_SIX_APPID)
                .addHeader("Ubi-SessionId", sessionId)
                .addHeader("Connection", "keep-alive");
    }

    public static Auth connect(String email, String password) {
        String auth_token = getBasicToken(email, password);
        ObjectNode post_body = new ObjectMapper().createObjectNode().put("rememberMe", true);
        JsonNode response = new ApiRequest("https://connect.ubi.com/ubiservices/v2/profiles/sessions")
                .addHeader("Ubi-AppId", RAINBOW_SIX_APPID)
                .addHeader("Authorization", "Basic " + auth_token)
                .post(post_body.toString());
        System.out.println("Authorized successfully: Continuing");
        return new Auth(response.get("content").get("ticket").asText(),
                response.get("content").get("sessionId").asText(),
                Instant.parse(response.get("content").get("expiration").asText()));
    }

    private static String getBasicToken(String email, String password) {
        return Base64.getEncoder().encodeToString((email + ":" + password).getBytes());
    }
}
