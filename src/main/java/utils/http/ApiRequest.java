package utils.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ApiRequest {

    private static HttpClient httpClient = HttpClientBuilder.create().build();

    private HttpResponse response;
    private StringBuilder url;
    private Map<String, String> parameters;

    public ApiRequest(String baseUrl) {
        this.url = new StringBuilder(baseUrl);
        parameters = new HashMap<>();
    }

    public ApiRequest setEndpoint(String endpoint) {
        url.append(endpoint);
        return this;
    }

    public ApiRequest setParameter(String parameter, String value) {
        parameters.put(parameter, value);
        return this;
    }

    public ApiRequest execute() {
        for (Map.Entry param : parameters.entrySet()) {
            url.append("&").append(param.getKey()).append("=").append(param.getValue());
        }
        String requestUrl = url.toString();
        requestUrl = requestUrl.replaceFirst("&", "?");
        HttpGet get = new HttpGet(requestUrl);
        get.addHeader("Content-Type", "application/json");
        try {
            response = httpClient.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
            response = null;
        }
        return this;

    }

    public HttpResponse getResponse() {
        return response;
    }

    public JsonNode getResponseContent() {
        try {
            return new ObjectMapper().readTree(response.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
