package modules.AutoRole;

import main.SpecialModule;

public class AutoRole implements SpecialModule {

    public boolean onLoad() {
        bot.registerHandlers(new AutoRoleHandler(bot));
        return true;
    }

    public String getName() {
        return "AutoRole";
    }

    public String getVersion() {
        return "1.0";
    }
}
