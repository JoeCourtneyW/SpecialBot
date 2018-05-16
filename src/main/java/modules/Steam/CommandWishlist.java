package modules.Steam;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.JsonObjects.GuildOptions;
import main.SpecialBot;

public class CommandWishlist extends CommandExecutor {
    public CommandWishlist(SpecialBot bot) {
        super(bot);
    }


    @Command(label="wishlist")
    public void wishlist(CommandEvent event){
        if(event.getArgs().length <= 1){
            event.reply("Enter an argument [add, remove, list]");
            return;
        }
        if(event.getArgs()[0].equalsIgnoreCase("add")){
            String query = event.getArgsAsString(1);
            Steam.searchService.submit(() -> {
                SteamGame steamGame = Steam.searchForGame(query);
                if(steamGame == null){
                    event.reply("No results found for the given query");
                    return;
                }

                GuildOptions options = bot.getGuildOptions(event.getGuild());
                options.WISHLIST.add(steamGame);
                bot.updateGuildOptions(options);
                event.reply("Added **" + steamGame.NAME + "** to the guilds steam wishlist!");
            });
        } else if(event.getArgs()[1].equalsIgnoreCase("remove")){

        } else if(event.getArgs()[1].equalsIgnoreCase("list")){
            GuildOptions options = bot.getGuildOptions(event.getGuild());
            for(SteamGame game : options.WISHLIST){

            }
        } else {
            event.reply("Enter an argument [add, remove, list]");
        }
    }
}
