package modules.Steam;

import main.SpecialBot;
import modules.SpecialModule;

public class Steam extends SpecialModule{

    private String name = "Steam";
    private String version = "1.0";

    public Steam(SpecialBot bot){
        super(bot);
    }

    public boolean enable() {
        registerCommands(new CommandSearch(bot));
        return true;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}