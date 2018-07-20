package main;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;

import java.io.IOException;

public class CommandUpdate implements CommandExecutor {

    @Command(label = "update", adminOnly = true)
    public void onUpdate(CommandEvent event) {
        if (event.getAuthor().getStringID().equalsIgnoreCase("107131529318117376")) { //SlyBro3#8695
            bot.getClient().logout();
            try {
                Runtime.getRuntime().exec("sudo reboot");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Command(label = "logout", adminOnly = true)
    public void onLogout(CommandEvent event) {
        if (event.getAuthor().getStringID().equalsIgnoreCase("107131529318117376")) { //SlyBro3#8695
            bot.getClient().logout();
        }
    }
}
