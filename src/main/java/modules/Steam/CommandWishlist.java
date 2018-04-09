package modules.Steam;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.Commands.Command;
import main.Commands.CommandExecutor;
import main.SpecialBot;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.EmbedBuilder;
import utils.LoggerUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class CommandWishlist extends CommandExecutor {
    private static SpecialBot bot;

    public CommandWishlist(SpecialBot bot) {
        super(bot);
        CommandWishlist.bot = bot;
    }

    @Command(label = "wishlist")
    public static void onWishlist(IMessage message) {
        String[] args = message.getContent().split(" ");
        IChannel channel = message.getChannel();
        InputStream stream;
        String appid = "378610"; //TODO: Reformat to make the try statements prettier, also add search functionality instead of simply the appid
        if(args.length >= 2){
            appid = args[1];
        }
        try {
            URL url = new URL("http://store.steampowered.com/api/appdetails/?appids=" + appid + "&cc=us&l=en");
            stream = url.openStream();
        } catch (IOException e) {
            LoggerUtil.CRITICAL("Shitty url kid");
            return;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(stream).get(appid);
            JsonNode pricing = root.get("data").get("price_overview");

            EmbedBuilder embed = new EmbedBuilder();

            embed.withThumbnail(root.get("data").get("header_image").asText());
            embed.withTitle(root.get("data").get("name").asText());
            embed.appendDesc(root.get("data").get("short_description").asText());
            if(pricing.get("discount_percent").asInt() != 0){
                embed.appendField("Price", "~~$" + pricing.get("initial").asDouble()/100 + "~~ **$" + pricing.get("final").asDouble()/100 + "**", true);
                embed.appendField("Discount", "__**-" + pricing.get("discount_percent").asText() + "%**__", true);
            } else {
                embed.appendField("Price", "$" + pricing.get("final").asDouble()/100,true);
            }
            embed.appendField("Buy Now", "http://store.steampowered.com/app/" + appid + "/", false);
            embed.withUrl("http://store.steampowered.com/app/" + appid + "/");
            bot.sendEmbed(embed.build(), channel);
        } catch (IOException e) {
            LoggerUtil.CRITICAL("PARSING ERROR");
        }
    }
}
