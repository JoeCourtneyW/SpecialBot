package modules.StatTracking;

import main.SpecialModule;

public class StatTracking implements SpecialModule {

    public boolean onLoad(){
        bot.registerCommands(new CommandFortnite());
        return true;
    }

    public String getName(){
        return "Stat Tracking";
    }
    public String getVersion(){
        return "1.0";
    }
}
