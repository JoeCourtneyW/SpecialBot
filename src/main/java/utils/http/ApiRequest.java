package utils.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ApiRequest {

    private static HttpClient httpClient = HttpClientBuilder.create()
            .setConnectionTimeToLive(10000, TimeUnit.MILLISECONDS).build();
    private static ObjectMapper objectMapper = new ObjectMapper();

    private StringBuilder url;
    private Map<String, String> parameters;
    private Map<String, String> headers;

    public ApiRequest(String baseUrl) {
        this.url = new StringBuilder(baseUrl);
        parameters = new HashMap<>();
        headers = new HashMap<>();
    }

    public ApiRequest setEndpoint(String endpoint) {
        url.append(endpoint);
        return this;
    }

    public ApiRequest setParameter(String parameter, String value) {
        parameters.put(parameter, value);
        return this;
    }
    public ApiRequest addHeader(String header, String value) {
        headers.put(header, value);
        return this;
    }
    public JsonNode post(String postEntity) {
        try {
            return post(new StringEntity(postEntity));
        } catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return null;
        }
    }

    public JsonNode post(HttpEntity postEntity) {
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            url.append("&").append(param.getKey()).append("=").append(param.getValue());
        }
        String requestUrl = url.toString();
        requestUrl = requestUrl.replaceFirst("&", "?");
        HttpPost req = new HttpPost(requestUrl);
        for (Map.Entry<String, String> header : headers.entrySet()) {
            req.addHeader(header.getKey(), header.getValue());
        }
        req.addHeader("Content-Type", "application/json");
        req.setEntity(postEntity);

        try {
            HttpResponse httpResponse = httpClient.execute(req);

            ObjectNode response = objectMapper.createObjectNode();
            String responseContent = IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");
            if(isJSONValid(responseContent)){
                JsonNode content = objectMapper.readTree(responseContent);
                response.set("content", content);
            } else {
                System.out.println(responseContent);
                response.put("content", responseContent);
            }
            response.put("status", httpResponse.getStatusLine().getStatusCode());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            ObjectNode response = objectMapper.createObjectNode();
            response.put("status", 500);
            response.set("content", objectMapper.createObjectNode());
            return response;
        }
    }

    public JsonNode get() {
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            url.append("&").append(param.getKey()).append("=").append(param.getValue());
        }
        String requestUrl = url.toString();
        requestUrl = requestUrl.replaceFirst("&", "?");
        HttpGet req = new HttpGet(requestUrl);
        req.addHeader("Content-Type", "application/json");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            req.addHeader(header.getKey(), header.getValue());
        }
        try {
            HttpResponse httpResponse = httpClient.execute(req);
            ObjectNode response = objectMapper.createObjectNode();
            String responseContent = IOUtils.toString(httpResponse.getEntity().getContent(), "UTF-8");
            if(isJSONValid(responseContent)){
                JsonNode content = objectMapper.readTree(responseContent);
                response.set("content", content);
            } else {
                System.out.println(responseContent);
                response.put("content", responseContent);
            }
            response.put("status", httpResponse.getStatusLine().getStatusCode());
            return response;
        } catch (IOException e) {
            e.printStackTrace();
            ObjectNode response = objectMapper.createObjectNode();
            response.put("status", 500);
            response.set("content", objectMapper.createObjectNode());
            return response;
        }

    }
    private boolean isJSONValid(String test) {
        try {
            new JSONObject(test);
        } catch (JSONException ex) {
            // edited, to include @Arthur's comment
            // e.g. in case JSONArray is valid as well...
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
