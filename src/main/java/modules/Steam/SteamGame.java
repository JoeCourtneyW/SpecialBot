package modules.Steam;


import com.fasterxml.jackson.databind.JsonNode;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.util.EmbedBuilder;

public class SteamGame {
    public String APPID;
    public String NAME;
    public boolean FREE;
    public double INITIAL_PRICE;
    public double FINAL_PRICE;
    public int DISCOUNT_PERCENT;
    public String HEADER_IMAGE;
    public String DEVELOPER;
    public String DESCRIPTION;
    public boolean RELEASED;
    public String RELEASE_DATE; // DD mmm, YYYY

    public String steamLink() {
        return "http://store.steampowered.com/app/" + APPID + "/";
    }

    public static SteamGame buildFromAppDetails(JsonNode appData) {
        if (appData.get("data") != null)
            appData = appData.get("data");
        SteamGame game = new SteamGame();
        game.APPID = appData.get("steam_appid").asText();
        game.NAME = appData.get("name").asText();
        game.FREE = appData.get("is_free").asBoolean();
        if (!game.FREE) {
            game.INITIAL_PRICE = appData.get("price_overview").get("initial").asInt() / 100.0;
            game.FINAL_PRICE = appData.get("price_overview").get("final").asInt() / 100.0;
            game.DISCOUNT_PERCENT = appData.get("price_overview").get("discount_percent").asInt();
        } else {
            game.INITIAL_PRICE = 0;
            game.FINAL_PRICE = 0;
            game.DISCOUNT_PERCENT = 0;
        }
        game.HEADER_IMAGE = appData.get("header_image").asText();
        game.DEVELOPER = appData.get("developers").get(0).asText();
        game.DESCRIPTION = appData.get("short_description").asText();
        game.RELEASED = appData.get("release_date").get("coming_soon").asBoolean();
        game.RELEASE_DATE = appData.get("release_date").get("date").asText();
        return game;
    }

    public EmbedObject buildEmbed() {
        EmbedBuilder embed = new EmbedBuilder();
        embed.withThumbnail(HEADER_IMAGE)
                .withTitle(NAME)
                .appendDesc(DESCRIPTION);

        if (!FREE) {
            if (DISCOUNT_PERCENT != 0) {
                embed.appendField("Price", "~~$" + INITIAL_PRICE + "~~ **$" + FINAL_PRICE + "**", true)
                        .appendField("Discount", "__**-" + DISCOUNT_PERCENT + "%**__", true);
            } else {
                embed.appendField("Price", "$" + FINAL_PRICE, true);
            }

            embed.appendField("Buy Now", steamLink(), false);
        } else {
            embed.appendField("Price", "**Free!**", true)
                    .appendField("Play Now", steamLink(), false);
        }
        embed.withUrl(steamLink());
        return embed.build();
    }
    public String formattedPrice(){
        if (!FREE) {
            if (DISCOUNT_PERCENT != 0)
                return "~~$" + INITIAL_PRICE + "~~ **$" + FINAL_PRICE + "**";
             else
                return "$" + FINAL_PRICE;
        } else {
            return "**Free!**";
        }
    }
}
