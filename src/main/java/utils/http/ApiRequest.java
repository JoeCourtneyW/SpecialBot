package utils.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApiRequest {

    private static HttpClient httpClient = HttpClientBuilder.create().build();
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
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            url.append("&").append(param.getKey()).append("=").append(param.getValue());
        }
        String requestUrl = url.toString();
        requestUrl = requestUrl.replaceFirst("&", "?");
        HttpPost req = new HttpPost(requestUrl);
        req.addHeader("Content-Type", "application/json");
        for (Map.Entry<String, String> header : headers.entrySet()) {
            req.addHeader(header.getKey(), header.getValue());
        }
        try {
            HttpResponse httpResponse = httpClient.execute(req);
            JsonNode content = objectMapper.readTree(httpResponse.getEntity().getContent());
            ObjectNode response = objectMapper.createObjectNode();
            response.set("content", content);
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
            JsonNode content = objectMapper.readTree(httpResponse.getEntity().getContent());
            ObjectNode response = objectMapper.createObjectNode();
            response.set("content", content);
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

}
