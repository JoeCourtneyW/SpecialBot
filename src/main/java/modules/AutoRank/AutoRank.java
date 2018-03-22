package modules.AutoRank;

import main.SpecialBot;
import modules.SpecialModule;

public class AutoRank extends SpecialModule {
    private String name = "AutoRank";
    private String version = "1.0";

    public AutoRank(SpecialBot bot) {
        super(bot);
    }

    public boolean enable() {
        registerHandlers(new AutoRankHandler(bot));
        return true;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
