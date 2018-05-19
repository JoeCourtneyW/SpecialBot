package modules.Miscellaneous;

import main.SpecialModule;

public class Miscellaneous implements SpecialModule {


    public boolean onLoad() {
        bot.registerCommands(new MiscCommands());
        return true;
    }

    public String getName() {
        return "Miscellaneous";
    }

    public String getVersion() {
        return "1.0";
    }
}
