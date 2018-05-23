package modules.StatTracking;

import com.fasterxml.jackson.databind.JsonNode;
import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.Main;
import sx.blah.discord.util.EmbedBuilder;
import utils.http.ApiRequest;
import utils.http.UrlUtil;

public class CommandFortnite implements CommandExecutor {

    @Command(label="fortnite")
    public void fortnite(CommandEvent event){
        if(event.getArgs().length == 0){
            event.reply("You must enter a username to search");
            return;
        }
        String username = UrlUtil.encodeURIComponent(event.getArgsAsString(0));
        JsonNode response = new ApiRequest("https://api.fortnitetracker.com")
                .setEndpoint("/v1/profile/pc/" + username)
                .addHeader("TRN-Api-Key", Main.CREDENTIALS.TRN_API_KEY)
                .get();
        if(response.get("status").asInt() != 200){
            event.reply("Player not found! They may not have an Epic Account set up");
            return;
        }
        EmbedBuilder embed = new EmbedBuilder()
        .withTitle(response.get("content").get("epicUserHandle").asText())
        .withImage("https://fortniteskins.net/wp-content/uploads/2018/03/tracker-outfit.png")
        .withUrl("https://fortnitetracker.com/profile/pc/" + response.get("content").get("epicUserHandle").asText());

        JsonNode lifetimeStats = response.get("content").get("lifeTimeStats");
        for(JsonNode stat : lifetimeStats){
            embed.appendField(stat.get("key").asText(), stat.get("value").asText(), true);
        }

        event.reply(embed.build());
    }
}
