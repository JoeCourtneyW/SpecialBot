package modules.Music;


import java.io.IOException;
import java.util.List;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

public class Search {

    /**
     * Define a global variable that identifies the developer's API key.
     */
    private static final String API_KEY = "AIzaSyDzrauzffk42JVWkkypTZHl7iIZE3bikQ4";

    private static final long NUMBER_OF_VIDEOS_RETURNED = 1;

    
    public static String[] getVideoID(String query) throws IOException{
    	// Define the API request for retrieving search results.
        YouTube.Search.List search = Music.youtube.search().list("id,snippet");

        // Set your developer key from the {{ Google Cloud Console }} for
        // non-authenticated requests. See:
        // {{ https://cloud.google.com/console }}
        String apiKey = API_KEY; 
        search.setKey(apiKey);
        search.setQ(query);

        // Restrict the search results to only include videos. See:
        // https://developers.google.com/youtube/v3/docs/search/list#type
        search.setType("video");

        // To increase efficiency, only retrieve the fields that the
        // application uses.
        search.setFields("items(id/videoId,snippet/title)");
        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);

        // Call the API
        SearchListResponse searchResponse = search.execute();
        List<SearchResult> searchResultList = searchResponse.getItems();
        String[] data = new String[2];
        data[0] = searchResultList.get(0).getId().getVideoId();
        data[1] = searchResultList.get(0).getSnippet().getTitle();
        return data;
        
    }
}