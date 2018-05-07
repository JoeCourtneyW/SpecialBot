package utils.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private HttpResponse response;
    private StringBuilder url;
    private Map<String, String> parameters;
    private Map<String, String> headers;

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
    public ApiRequest addHeader(String header, String value) {
        headers.put(header, value);
        return this;
    }

    public ApiRequest post(String postEntity) {
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
            response = httpClient.execute(req);
        } catch (IOException e) {
            e.printStackTrace();
            response = null;
        }
        return this;
    }

    public ApiRequest get() {
        for (Map.Entry<String, String> param : parameters.entrySet()) {
            url.append("&").append(param.getKey()).append("=").append(param.getValue());
        }
        String requestUrl = url.toString();
        requestUrl = requestUrl.replaceFirst("&", "?");
        HttpGet req = new HttpGet(requestUrl);
        req.addHeader("Content-Type", "application/json");
        try {
            response = httpClient.execute(req);
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
