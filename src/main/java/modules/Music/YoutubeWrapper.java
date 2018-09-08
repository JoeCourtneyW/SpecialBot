package modules.Music;


import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchResult;
import main.Main;
import utils.Pair;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class YoutubeWrapper {

    private Music music;

    public YoutubeWrapper(Music music) {
        this.music = music;
    }

    private final String API_KEY = Main.CREDENTIALS.GOOGLE_API_KEY;

    public Pair<String, String> searchForVideo(String query) throws IOException {
        // Define the API request for retrieving search results.
        YouTube.Search.List search = music.getYoutube().search().list("id,snippet");

        search.setQ(query);

        search.setKey(API_KEY);

        // Restrict the search results to only include videos. See:
        // https://developers.google.com/youtube/v3/docs/search/list#type
        search.setType("video");

        // To increase efficiency, only retrieve the fields that the
        // application uses.
        search.setFields("items(id/videoId,snippet/title)");
        search.setMaxResults(1L);

        // Call the API
        List<SearchResult> searchResultList = search.execute().getItems();
        String id = searchResultList.get(0).getId().getVideoId();
        String title = searchResultList.get(0).getSnippet().getTitle();
        return new Pair<>(id, title);
    }

    public String getVideoTitle(String id) {
        try {
            YouTube.Videos.List list = music.getYoutube().videos().list("snippet");
            list.setId(id);
            list.setKey(API_KEY);
            return list.execute().getItems().get(0).getSnippet().getTitle();
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public long getVideoDuration(String id) {
        try {
            YouTube.Videos.List list = music.getYoutube().videos().list("contentDetails");
            list.setId(id);
            list.setKey(API_KEY);
            return Duration.parse(list.execute().getItems().get(0).getContentDetails().getDuration()).toMillis();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static String getIdFromUrl(String url) {
        //TODO: Craft regex to determine if the url given is a youtube url
        String id;
        if (url.contains("youtu.be")) {
            id = url.split("/")[1];
        } else {
            id = url.split("\\?v=")[1];
            if (id.contains("&"))
                id = id.split("&")[0];
        }
        return id;
    }
}