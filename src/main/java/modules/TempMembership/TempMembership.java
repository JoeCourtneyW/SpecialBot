package modules.TempMembership;

import main.SpecialBot;
import modules.SpecialModule;

public class TempMembership extends SpecialModule{
    private String name = "Temporary Membership";
    private String version = "1.0";

    private MembershipTimer timer;

    public TempMembership(SpecialBot bot){
        super(bot);
    }

    public boolean enable() {
        this.timer = new MembershipTimer(bot.getClient());
        bot.getAsyncExecutor().submit(() -> {
           timer.start();
        });
        return true;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
