package modules.StatTracking;

import main.SpecialModule;
import utils.http.RequestPool;

public class StatTracking implements SpecialModule {

    public static StatTracking instance;

    public RequestPool fortniteRequestPool;

    public boolean onLoad(){
        instance = this;

        fortniteRequestPool = new RequestPool(2);

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
