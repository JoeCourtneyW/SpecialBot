package modules.AutoRole;

import main.SpecialBot;
import modules.SpecialModule;

public class AutoRole extends SpecialModule {
    private String name = "AutoRole";
    private String version = "1.1";

    public AutoRole(SpecialBot bot) {
        super(bot);
    }

    public boolean enable() {
        registerHandlers(new AutoRoleHandler(bot));
        registerCommands(new DefaultRoleCommand(bot));
        return true;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
