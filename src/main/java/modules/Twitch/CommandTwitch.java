package modules.Twitch;

import com.fasterxml.jackson.databind.JsonNode;
import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import sx.blah.discord.util.EmbedBuilder;
import utils.http.ApiRequest;

import java.text.NumberFormat;
import java.util.Locale;

public class CommandTwitch implements CommandExecutor {
    @Command(label = "twitch")
    public static void onTwitch(CommandEvent event) {
        if(event.getArgs().length < 1){
            event.reply("Enter a streamer's name as the first argument");
            return;
        }

        String channelName = event.getArgs()[0];
        JsonNode response = new ApiRequest("https://api.twitch.tv").setEndpoint("/kraken/channels/" + channelName)
                .addHeader("Client-ID", "17vs6h16pb0esv8gushd351lm5ln9t") //Client IDs can be shared publicly
                .get();

        if(response.get("status").asInt() != 200) {
            event.reply("Streamer not found");
            return;
        }

        JsonNode channel = response.get("content");


        EmbedBuilder embed = new EmbedBuilder()
        .withTitle(channel.get("display_name").asText())
        .withUrl("http://twitch.tv/" + channel.get("display_name").asText())
        .withThumbnail(channel.get("logo").asText());

        if(channel.get("status").asText() != null)
        embed.withDescription(channel.get("status").asText());

        NumberFormat numberFormatter = NumberFormat.getInstance(Locale.getDefault());

        embed.appendField("Followers", numberFormatter.format(channel.get("followers").asInt()) + "", true)
        .appendField("Total Views", numberFormatter.format(channel.get("views").asInt()) + "", true)
        .appendField("Partnered", (channel.get("partner").asBoolean() ? "Yes" : "No"), false);

        /* This is the code to grab a new user object for the NEW Twitch API
        System.out.println(new ApiRequest("https://api.twitch.tv").setEndpoint("/helix/users")
                .addHeader("Client-ID", "CLIENT_ID")
                .setParameter("login", "timthetatman")
                .get().get("content").get("data").get(0).get("view_count").asInt());
        System.out.println(.get("content").get("_total").asInt()); */

        event.reply(embed.build());
    }
}
