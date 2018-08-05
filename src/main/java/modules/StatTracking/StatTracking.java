package modules.StatTracking;

import main.Main;
import main.SpecialModule;
import main.com.github.courtneyjoew.Auth;
import main.com.github.courtneyjoew.R6J;
import utils.http.RequestPool;

public class StatTracking implements SpecialModule {

    /* TODO
     Active tracking: allow guilds to have a list of users whose stats are periodically checked and generate small
     plots based on gathered data points to show stats over time. Generate reports at the end of each day and
     private message them to the users being tracked detailing their progress over the past 24 hours if they had any
     */
    public static StatTracking instance;

    public RequestPool fortniteRequestPool;
    public R6J rainbowSix;

    public boolean onLoad() {
        instance = this;
        fortniteRequestPool = new RequestPool(2);
        rainbowSix = new R6J(
                new Auth(Main.CREDENTIALS.RAINBOW_SIX_EMAIL,
                        Main.CREDENTIALS.RAINBOW_SIX_PASSWORD));

        bot.registerCommands(new CommandFortnite());
        bot.registerCommands(new CommandRainbow());
        return true;
    }

    public String getName() {
        return "Stat Tracking";
    }

    public String getVersion() {
        return "1.0";
    }

    public R6J getRainbowSix() {
        return rainbowSix;
    }
}
