package modules.StatTracking;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;

public class CommandRainbow implements CommandExecutor {

    @Command(label = "rainbow")
    public void rainbow(CommandEvent event) {
        if (event.getArgs().length == 0) {
            event.reply("You must enter a username to search");
            return;
        }

        //TODO
    }
}
