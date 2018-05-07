package modules.Miscellaneous;

import com.fasterxml.jackson.databind.JsonNode;
import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.SpecialBot;
import org.apache.http.HttpException;
import utils.http.ApiRequest;

public class MiscCommands extends CommandExecutor {
    public MiscCommands(SpecialBot bot) {
        super(bot);
    }
    @Command(label="quote")
    public void quoteCommand(CommandEvent event) throws HttpException{
        ApiRequest req = new ApiRequest("http://quotesondesign.com").setEndpoint("/wp-json/posts")
                .setParameter("filter[orderby]", "rand")
                .setParameter("filter[posts_per_page]", "1")
                .get();
        int status = req.getResponse().getStatusLine().getStatusCode();
        JsonNode node = req.getResponseContent();
        String quote = node.get(0).get("content").asText();
        if(status == 200)
            bot.sendChannelMessage("*\"" + quote.substring(3, quote.length()-7) + "\"* - " + node.get(0).get("title").asText(), event.getChannel());
        else
            throw new HttpException("Quote website error: Status code:" + status);

    }
}
