package utils.http;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.io.InputStream;

public class SlyHTTPRequest {
    private static HttpClient httpClient = HttpClientBuilder.create().build();
    private HttpResponse response;
    private HttpRequestBase request;

    public SlyHTTPRequest(String url, RequestType type) {
        if (type == RequestType.POST) {
            request = new HttpPost(url);
        } else {
            request = new HttpGet(url);
        }
    }

    public SlyHTTPRequest contentType(String contentType) {
        request.addHeader("Content-Type", contentType);
        return this;
    }

    public SlyHTTPRequest addHeader(String header, String value) {
        request.addHeader(header, value);
        return this;
    }

    public SlyHTTPRequest setEntity(String entity) {
        try {
            ((HttpPost) request).setEntity(new StringEntity(entity));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public SlyHTTPRequest execute() {
        try {
            response = httpClient.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public HttpResponse getResponse() {
        return response;
    }

    public String getResponseContent() {
        String content = "";
        try {
            InputStream in = response.getEntity().getContent();
            content = IOUtils.toString(in, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public enum RequestType {
        POST,
        GET
    }
}
