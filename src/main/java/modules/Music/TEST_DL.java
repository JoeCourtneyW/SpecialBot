package modules.Music;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.text.StringEscapeUtils;
import utils.http.ApiRequest;

public class TEST_DL {

    public static void main(String[] args) {
        /*// Video url to download
        String videoUrl = "https://www.youtube.com/watch?v=FxYw0XPEoKE";
        // Destination directory
        String directory = System.getProperty("user.dir");

        // Build request
        YoutubeDLRequest request = new YoutubeDLRequest(videoUrl, directory);
        request.setOption("id");
        request.setOption("extract-audio");
        request.setOption("audio-format", "mp3");
        request.setOption("ignore-errors");
        request.setOption("retries", 2);

        // Make request and return response
        try {
            YoutubeDLResponse response = YoutubeDL.execute(request);

            System.out.println(response.getOut());
        } catch (Exception e) {
            e.printStackTrace();
        }
*/
       /* try {
            String access_token = "";
            String longUrl = "http%3A%2F%2Fgoogle.com%2F";
            String url = "https://api-ssl.bitly.com/v3/shorten?access_token=" + access_token + "&longUrl=" + longUrl;
            SlyHTTPRequest req = new SlyHTTPRequest(url, SlyHTTPRequest.RequestType.GET)
                    .contentType("application/json")
                    .execute();
            System.out.println(req.getResponse().getStatusLine());
            System.out.println(req.getResponseContent());
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        ApiRequest req = new ApiRequest("http://quotesondesign.com").setEndpoint("/wp-json/posts")
                .setParameter("filter[orderby]", "rand")
                .setParameter("filter[posts_per_page]", "1")
                .execute();
        System.out.println(req.getResponse().getStatusLine());
        JsonNode node = req.getResponseContent();
        System.out.println(StringEscapeUtils.unescapeHtml4(node.get(0).get("content").asText().substring(3).split("</p>")[0]));
    }
}