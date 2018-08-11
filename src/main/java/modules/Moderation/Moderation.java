package modules.Moderation;

import main.SpecialModule;

public class Moderation implements SpecialModule {

    public boolean onLoad() {

        return true;
    }

    public String getName() {
        return "Moderation";
    }

    public String getVersion() {
        return "1.0";
    }
}
