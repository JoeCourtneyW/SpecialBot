package modules.TempMembership;

import main.JsonObjects.GuildOptions;
import main.SpecialBot;
import modules.SpecialModule;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import utils.LoggerUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TempMembership extends SpecialModule {
    private String name = "Temporary Membership";
    private String version = "1.1";


    public TempMembership(SpecialBot bot) {
        super(bot);
    }

    public boolean enable() { //TODO: Try using guild#getUserJoinDate
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            String hour = new SimpleDateFormat("HH").format(new Date()); //Grabs the 24 hour formatted hour that it currently is
            if (Integer.valueOf(hour) == 6) {
                //It's part of the 6AM hour, start kicking members who have overstayed their welcome
                for (IGuild guild : bot.getClient().getGuilds()) {
                    GuildOptions guildOptions = bot.getGuildOptions(guild);
                    if (guildOptions.AUTO_KICK && !guildOptions.DEFAULT_ROLE.isEmpty()) {
                        for (IUser user : guild.getUsers()) {
                            if (user.hasRole(guild.getRoleByID(Long.parseLong(guildOptions.DEFAULT_ROLE)))) { //they are still default role
                                LoggerUtil.INFO("Default role found, removing from the server");
                                guild.kickUser(user, "");
                                //TODO: Try to use IUser#getCreationDate to see if that shows when they joined the guild
                            }
                        }
                    }
                }
            }
        }, 1, 1, TimeUnit.HOURS);
        return true;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
