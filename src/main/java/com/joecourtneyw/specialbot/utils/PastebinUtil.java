package com.joecourtneyw.specialbot.utils;

import com.fasterxml.jackson.databind.JsonNode;
import main.Main;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import utils.http.ApiRequest;

public class PastebinUtil {
    private static String API_KEY = Main.CREDENTIALS.PASTEBIN_API_KEY;

    public static String createPaste(String title, String format, String content) {

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addTextBody("api_option", "paste", ContentType.TEXT_PLAIN);
        builder.addTextBody("api_dev_key", API_KEY, ContentType.TEXT_PLAIN);
        builder.addTextBody("api_paste_name", title, ContentType.TEXT_PLAIN);
        builder.addTextBody("api_paste_format", format, ContentType.TEXT_PLAIN);
        builder.addTextBody("api_paste_code", content, ContentType.TEXT_PLAIN);
        HttpEntity reqEntity = builder.build();
        JsonNode response = new ApiRequest("https://pastebin.com/api").setEndpoint("/api_post.php")
                .post(reqEntity);
        return response.get("content").asText();
    }
}
