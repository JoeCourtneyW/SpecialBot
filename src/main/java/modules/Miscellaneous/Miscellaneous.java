package modules.Miscellaneous;

import main.SpecialBot;
import modules.SpecialModule;

public class Miscellaneous extends SpecialModule {
    private String name = "Misc Module";
    private String version = "1.0";

    public Miscellaneous(SpecialBot bot) {
        super(bot);
    }

    public boolean enable() {
        registerCommands(new MiscCommands(bot));
        return true;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
