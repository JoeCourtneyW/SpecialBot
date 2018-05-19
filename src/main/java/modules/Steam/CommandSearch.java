package modules.Steam;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;

public class CommandSearch implements CommandExecutor {

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
            event.reply(steamGame.buildEmbed());
        });
    }


}
