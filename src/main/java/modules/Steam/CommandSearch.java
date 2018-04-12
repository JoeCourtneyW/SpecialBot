package modules.Steam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.Commands.Command;
import main.Commands.CommandExecutor;
import main.SpecialBot;
import org.simmetrics.StringMetric;
import org.simmetrics.builders.StringMetricBuilder;
import org.simmetrics.metrics.*;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import utils.LoggerUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.StringJoiner;

public class CommandSearch extends CommandExecutor {
    private static SpecialBot bot;
    private static StringMetric overlap_block;

    public CommandSearch(SpecialBot bot) {
        super(bot);
        CommandSearch.bot = bot;
        overlap_block = StringMetricBuilder.with(new OverlapCoefficient<>())
                .simplify(Simplifiers.toLowerCase())
                .simplify(Simplifiers.removeNonWord())
                .tokenize(Tokenizers.whitespace())
                .build();
    }

    @Command(label = "search")
    public static void onSearch(IMessage message) {
        String[] args = message.getContent().split(" ");
        IChannel channel = message.getChannel();

        String query;
        if (args.length >= 2) {
            StringJoiner searchQuery = new StringJoiner(" ");
            for (int i = 1; i < args.length; i++) {
                searchQuery.add(args[i]);
            }
            query = searchQuery.toString();
        } else {
            bot.sendChannelMessage("You must enter a search term", channel);
            return;
        }
        bot.getAsyncExecutor().submit(() -> {
            JsonNode game = searchForGame(query);

            String appid = game.get("appid").asText();

            InputStream stream = getStreamFromUrl("http://store.steampowered.com/api/appdetails/?appids=" + appid + "&cc=us&l=en");
            JsonNode root = getRootNode(stream).get(appid);
            JsonNode pricing = root.get("data").get("price_overview");
            LoggerUtil.DEBUG(root.toString());
            EmbedBuilder embed = new EmbedBuilder();

            embed.withThumbnail(root.get("data").get("header_image").asText());
            embed.withTitle(root.get("data").get("name").asText());
            embed.appendDesc(root.get("data").get("short_description").asText());
            if (pricing.get("discount_percent").asInt() != 0) {
                embed.appendField("Price", "~~$" + pricing.get("initial").asDouble() / 100 + "~~ **$" + pricing.get("final").asDouble() / 100 + "**", true);
                embed.appendField("Discount", "__**-" + pricing.get("discount_percent").asText() + "%**__", true);
            } else {
                embed.appendField("Price", "$" + pricing.get("final").asDouble() / 100, true);
            }
            embed.appendField("Buy Now", "http://store.steampowered.com/app/" + appid + "/", false);
            embed.withUrl("http://store.steampowered.com/app/" + appid + "/");
            bot.sendEmbed(embed.build(), channel);
        });
    }

    /**
     * @param query What to look up
     * @return The appid that matches closest to the given query
     */
    public static JsonNode searchForGame(String query) {
        StringMetric metric = StringMetricBuilder.with(new SimonWhite<>())
                .simplify(Simplifiers.toLowerCase())
                .tokenize(Tokenizers.whitespace())
                .build();
        InputStream gameListStream = getStreamFromUrl("http://api.steampowered.com/ISteamApps/GetAppList/v0002/");
        JsonNode root = getRootNode(gameListStream).get("applist");

        JsonNode bestResult = null;
        float bestSimilarity = 0;

        float similarity;
        for (JsonNode game : root.get("apps")) {
            if (query.equalsIgnoreCase(game.get("name").asText())) {
                return game;
            }
            similarity = metric.compare(query, game.get("name").asText());
            System.out.println(game.get("name") + ": " + similarity);
            if (similarity > bestSimilarity) {
                bestResult = game;
                bestSimilarity = similarity;
            } else if (similarity == bestSimilarity) {
                if(bestResult == null)
                    continue;
                if (game.get("name").asText().length() < bestResult.get("name").asText().length()) {
                    bestResult = game;
                }
            }
        }
        System.out.println("Best: " + bestResult.get("name").asText() + " (" + bestSimilarity + ")");
        return bestResult;
    }

    private static InputStream getStreamFromUrl(String url) {
        try {
            return new URL(url).openStream();
        } catch (IOException e) {
            LoggerUtil.CRITICAL("Failed to open stream to url: " + url);
            return null;
        }
    }

    private static JsonNode getRootNode(InputStream stream) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(stream);
        } catch (IOException e) {
            LoggerUtil.CRITICAL("Can't open root node of stream: " + e.getMessage());
            return null;
        }
    }
}
