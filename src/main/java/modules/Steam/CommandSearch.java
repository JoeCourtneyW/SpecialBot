package modules.Steam;

import com.fasterxml.jackson.databind.JsonNode;
import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.SpecialBot;
import org.simmetrics.StringMetric;
import org.simmetrics.builders.StringMetricBuilder;
import org.simmetrics.metrics.SimonWhite;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;
import sx.blah.discord.util.EmbedBuilder;
import utils.LoggerUtil;
import utils.http.ApiRequest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandSearch extends CommandExecutor {
    private static SpecialBot bot;
    private StringMetric searchMetric;

    public CommandSearch(SpecialBot bot) {
        super(bot);
        CommandSearch.bot = bot;
        searchMetric = StringMetricBuilder.with(new SimonWhite<>())
                .simplify(Simplifiers.toLowerCase())
                .simplify(Simplifiers.removeAll("[^A-Za-z0-9 ]+")) //TODO: Remove regex because regices are slooooooow
                .tokenize(Tokenizers.whitespace())
                .build();
    }

    @Command(label = "search")
    public void onSearch(CommandEvent event) {
        String query = event.getArgsAsString(0).trim();
        if (query.isEmpty()) {
            bot.sendChannelMessage("You must enter a search term", event.getChannel());
            return;
        }

        ExecutorService asyncSearch = Executors.newFixedThreadPool(1);
        asyncSearch.submit(() -> {
            JsonNode game = searchForGame(query);
            if(game == null){
                bot.sendChannelMessage("No results found", event.getChannel());
                asyncSearch.shutdown();
                return;
            }
            String appid = game.get("appid").asText();
            ApiRequest request = new ApiRequest("http://store.steampowered.com/api").setEndpoint("/appdetails/")
                    .setParameter("appids", appid)
                    .setParameter("cc", "us")
                    .setParameter("l", "en")
                    .get();
            JsonNode root = request.getResponseContent().get(appid);
            JsonNode pricing = root.get("data").get("price_overview");

            LoggerUtil.DEBUG(root.toString());

            EmbedBuilder embed = new EmbedBuilder();
            embed.withThumbnail(root.get("data").get("header_image").asText())
                    .withTitle(root.get("data").get("name").asText())
                    .appendDesc(root.get("data").get("short_description").asText());

            if (pricing != null) {
                if (pricing.get("discount_percent").asInt() != 0) {
                    embed.appendField("Price", "~~$" + pricing.get("initial").asDouble() / 100 + "~~ **$" + pricing.get("final").asDouble() / 100 + "**", true)
                            .appendField("Discount", "__**-" + pricing.get("discount_percent").asText() + "%**__", true);
                } else {
                    embed.appendField("Price", "$" + pricing.get("final").asDouble() / 100, true);
                }
            } else {
                embed.appendField("Price", "**Free!**", true);
            }

            embed.appendField("Buy Now", "http://store.steampowered.com/app/" + appid + "/", false)
                    .withUrl("http://store.steampowered.com/app/" + appid + "/");
            bot.sendEmbed(embed.build(), event.getChannel());
            asyncSearch.shutdown();
        });
    }

    /**
     * @param query What to look up
     * @return The appid that matches closest to the given query
     */
    private JsonNode searchForGame(String query) {
        ApiRequest request = new ApiRequest("http://api.steampowered.com")
                .setEndpoint("/ISteamApps/GetAppList/v0002/")
                .get();
        JsonNode applist = request.getResponseContent().get("applist");

        JsonNode bestResult = null;
        float bestSimilarity = 0;

        float similarity;
        for (JsonNode game : applist.get("apps")) {
            if (query.equalsIgnoreCase(game.get("name").asText())) {
                return game;
            }
            similarity = searchMetric.compare(query, game.get("name").asText());
            if (similarity > .5)
                System.out.println(game.get("name") + ": " + similarity);
            if (similarity > bestSimilarity) {
                bestResult = game;
                bestSimilarity = similarity;
            } else if (similarity == bestSimilarity) {
                if (bestResult == null)
                    continue;
                if (game.get("name").asText().length() < bestResult.get("name").asText().length()) {
                    bestResult = game;
                }
            }
        }
        //System.out.println("Best: " + bestResult.get("name").asText() + " (" + bestSimilarity + ")");
        return bestResult;
    }
}
