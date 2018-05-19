package modules.Twitch;

import main.SpecialModule;

public class Twitch implements SpecialModule {

    public boolean onLoad() {
        bot.registerCommands(new CommandTwitch());
        return true;
    }

    public String getName() {
        return "Reddit";
    }

    public String getVersion() {
        return "1.0";
    }
}
