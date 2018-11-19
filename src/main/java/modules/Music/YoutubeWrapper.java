package modules.Music;


import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import main.Main;
import utils.Pair;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
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

    public List<PlaylistItem> getSongsFromPlaylist(String id) throws IOException {
        // Define a list to store items in the list of uploaded videos.
        List<PlaylistItem> playlistItemList = new ArrayList<>();

        YouTube.PlaylistItems.List playlistItemRequest =
                Music.instance.getYoutube().playlistItems().list("id,contentDetails,snippet");
        playlistItemRequest.setKey(API_KEY);
        playlistItemRequest.setPlaylistId(id);
        playlistItemRequest.setFields(
                "items(contentDetails/videoId,snippet/title),nextPageToken,pageInfo");

        String nextToken = "";

        // Call the API one or more times to retrieve all items in the
        // list. As long as the API response returns a nextPageToken,
        // there are still more items to retrieve.
        do {
            playlistItemRequest.setPageToken(nextToken);
            PlaylistItemListResponse playlistItemResult = playlistItemRequest.execute();

            playlistItemList.addAll(playlistItemResult.getItems());

            nextToken = playlistItemResult.getNextPageToken();
        } while (nextToken != null);

        return playlistItemList;
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

    public static String getVideoIdFromUrl(String url) {
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

    public static String getPlaylistIdFromUrl(String url) {
        return url.split("&list=")[1]; //TODO, safer way to do this omega
    }
}