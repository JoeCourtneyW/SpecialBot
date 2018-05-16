package modules.Steam;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.SpecialBot;
import sx.blah.discord.util.EmbedBuilder;

public class CommandSearch extends CommandExecutor {

    public CommandSearch(SpecialBot bot) {
        super(bot);
    }

    @Command(label = "search")
    public void onSearch(CommandEvent event) {
        String query = event.getArgsAsString(0).trim();
        if (query.isEmpty()) {
            event.reply("You must enter a search term");
            return;
        }

        Steam.searchService.submit(() -> {
            SteamGame steamGame = Steam.searchForGame(query);
            if (steamGame == null) {
                event.reply("No results found");
                return;
            }

            EmbedBuilder embed = new EmbedBuilder();
            embed.withThumbnail(steamGame.HEADER_IMAGE)
                    .withTitle(steamGame.NAME)
                    .appendDesc(steamGame.DESCRIPTION);

            if (!steamGame.FREE) {
                if (steamGame.DISCOUNT_PERCENT != 0) {
                    embed.appendField("Price", "~~$" + steamGame.INITIAL_PRICE + "~~ **$" + steamGame.FINAL_PRICE + "**", true)
                            .appendField("Discount", "__**-" + steamGame.DISCOUNT_PERCENT + "%**__", true);
                } else {
                    embed.appendField("Price", "$" + steamGame.FINAL_PRICE, true);
                }

                embed.appendField("Buy Now", steamGame.steamLink(), false);
            } else {
                embed.appendField("Price", "**Free!**", true)
                        .appendField("Play Now", steamGame.steamLink(), false);
            }
            embed.withUrl(steamGame.steamLink());
            event.reply(embed.build());
        });
    }


}
