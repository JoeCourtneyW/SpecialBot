package modules.Steam;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.GuildOptions.GuildOptions;
import sx.blah.discord.util.EmbedBuilder;

public class CommandWishlist implements CommandExecutor {

    @Command(label = "wishlist")
    public void wishlist(CommandEvent event) {
        if (event.getArgs().length < 1) {
            event.reply("Enter an argument [add, remove, list]");
            return;
        }
        if (event.getArgs()[0].equalsIgnoreCase("add")) {
            String query = event.getArgsAsString(1);
            Steam.searchService.submit(() -> {
                SteamGame steamGame = Steam.searchForGame(query);
                if (steamGame == null) {
                    event.reply("No results found for the given query");
                    return;
                }

                GuildOptions options = bot.getGuildOptions(event.getGuild());
                options.WISHLIST.add(steamGame);
                bot.updateGuildOptions(options);
                event.reply("Added **" + steamGame.NAME + "** to the guild's steam wishlist!");
                event.reply(steamGame.buildEmbed());
            });
        } else if (event.getArgs()[0].equalsIgnoreCase("remove")) {
            String query = event.getArgsAsString(1);
            Steam.searchService.submit(() -> {
                SteamGame steamGame = Steam.searchForGame(query);
                if (steamGame == null) {
                    event.reply("No results found for the given query");
                    return;
                }
                GuildOptions options = bot.getGuildOptions(event.getGuild());
                for (SteamGame game : options.WISHLIST) {
                    if (game.APPID.equalsIgnoreCase(steamGame.APPID)) {
                        options.WISHLIST.remove(game);
                        bot.updateGuildOptions(options);
                        event.reply("Removed **" + steamGame.NAME + "** from the guild's steam wishlist!");
                        return;
                    }
                }

                event.reply("**" + steamGame.NAME + "** is not a part of your guild's wishlist!");
            });
        } else if (event.getArgs()[0].equalsIgnoreCase("list")) {
            GuildOptions options = bot.getGuildOptions(event.getGuild());
            EmbedBuilder embed = new EmbedBuilder();
            embed.withTitle("Guild Wishlist");
            for (SteamGame game : options.WISHLIST) {
                embed.appendField(game.NAME, game.formattedPrice(), true);
            }

            bot.sendEmbed(embed.build(), event.getChannel());
        } else {
            event.reply("Enter an argument [add, remove, list]");
        }
    }
}
