package main;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;

import java.io.IOException;

public class CommandUpdate extends CommandExecutor {
    public CommandUpdate(SpecialBot bot) {
        super(bot);
    }

    @Command(label = "update", adminOnly = true)
    public void onUpdate(CommandEvent event) {
        if (event.getAuthor().getStringID().equalsIgnoreCase("107131529318117376")) {
            bot.getClient().logout();
            try {
                Runtime.getRuntime().exec("sudo reboot");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
