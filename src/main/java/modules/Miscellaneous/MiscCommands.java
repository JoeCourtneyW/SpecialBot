package modules.Miscellaneous;

import com.fasterxml.jackson.databind.JsonNode;
import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.Main;
import main.SpecialBot;
import org.apache.http.HttpException;
import utils.http.ApiRequest;

public class MiscCommands extends CommandExecutor {
    public MiscCommands(SpecialBot bot) {
        super(bot);
    }

    @Command(label = "quote")
    public void quoteCommand(CommandEvent event) throws HttpException {
        JsonNode response = new ApiRequest("http://quotesondesign.com").setEndpoint("/wp-json/posts")
                .setParameter("filter[orderby]", "rand")
                .setParameter("filter[posts_per_page]", "1")
                .get();
        int status = response.get("status").asInt();
        String quote = response.get("content").get(0).get("content").asText();
        if (status == 200) //Trimming five chars from quote length for "\n" & "<p>" from html content
            event.reply("*\"" + quote.substring(3, quote.length() - 6).trim() + "\"* - " + response.get("content").get(0).get("title").asText());
        else
            throw new HttpException("Quote website error: Status code:" + status);

    }

    @Command(label = "shorten", description = "Shorten a given url using BITLY")
    public void shortenCommand(CommandEvent event) throws HttpException {
        if (event.getArgs().length == 0) {
            event.reply("You must enter a link to shorten");
            return;
        }
        String longUrl = event.getArgs()[0];
        JsonNode response = new ApiRequest("https://api-ssl.bitly.com").setEndpoint("/v3/shorten")
                .setParameter("access_token", Main.CREDENTIALS.BITLY_KEY)
                .setParameter("longUrl", longUrl)
                .get();
        int status = response.asInt();
        if (status == 200)
            event.reply("Shortened Link: " + response.get("content").get("data").get("url").asText());
        else
            throw new HttpException("Link Shorten error: Status code:" + status);

    }
}
