package modules.Steam;

import com.fasterxml.jackson.databind.JsonNode;
import main.SpecialModule;
import org.simmetrics.StringMetric;
import org.simmetrics.builders.StringMetricBuilder;
import org.simmetrics.metrics.SimonWhite;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;
import utils.http.ApiRequest;

import javax.annotation.Nullable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Steam implements SpecialModule {

    static Steam instance;

    static StringMetric //Build my own search metric: Match length of query to sub sections of match with low levenshteins
            searchMetric = StringMetricBuilder.with(new SimonWhite<>())
            .simplify(Simplifiers.toLowerCase())
            .simplify(Simplifiers.removeNonWord())
            .tokenize(Tokenizers.whitespace())
            .build();
    static JsonNode appList = new ApiRequest("http://api.steampowered.com")
            .setEndpoint("/ISteamApps/GetAppList/v0002/")
            .get().get("content").get("applist");

    static ExecutorService searchService = Executors.newCachedThreadPool();

    public boolean onLoad() {
        bot.registerCommands(new CommandSearch(), new CommandWishlist());
        instance = this;
        return true;
    }

    public String getName() {
        return "Steam";
    }

    public String getVersion() {
        return "1.0";
    }

    /**
     * @param query What to look up
     * @return The steamgame object that best matches the query
     */
    @Nullable
    public static SteamGame searchForGame(String query) {

        JsonNode bestResult = null;
        float bestSimilarity = 0;
        float similarity;

        for (JsonNode game : appList.get("apps")) {
            if (query.equalsIgnoreCase(game.get("name").asText())) {
                bestResult = game;
                continue;
            }
            similarity = searchMetric.compare(query, game.get("name").asText());

            if (similarity > bestSimilarity) { //If this is more similar than the most similar so far
                bestResult = game;
                bestSimilarity = similarity;
            } else if (similarity == bestSimilarity && similarity != 0) { //If the similarity is equivalent and it's not 0
                if (game.get("name").asText().length() < bestResult.get("name").asText().length()) {
                    bestResult = game; //If they are both very similar, take shorter result
                }
            }
        }

        if (bestResult == null)
            return null;
        return getAppDetails(bestResult.get("appid").asText());
    }

    private static SteamGame getAppDetails(String appid) {
        JsonNode response = new ApiRequest("http://store.steampowered.com/api").setEndpoint("/appdetails/")
                .setParameter("appids", appid)
                .setParameter("cc", "us")
                .setParameter("l", "en")
                .get();

        return SteamGame.buildFromAppDetails(response.get("content").get(appid).get("data"));
    }
}