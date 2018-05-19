package modules.TempMembership;

import main.GuildOptions.GuildOptions;
import main.SpecialModule;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import utils.LoggerUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TempMembership implements SpecialModule {

    private final long length = 1000 * 60 * 60 * 24;

    public boolean onLoad() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            for (IGuild guild : bot.getClient().getGuilds()) {
                GuildOptions guildOptions = bot.getGuildOptions(guild);
                if (guildOptions.AUTO_KICK && !guildOptions.DEFAULT_ROLE.isEmpty()) {
                    for (IUser user : guild.getUsers()) {
                        if (user.hasRole(guild.getRoleByID(Long.parseLong(guildOptions.DEFAULT_ROLE)))) { //they are still default role
                            if (guild.getJoinTimeForUser(user).toEpochMilli() - System.currentTimeMillis() > length) {
                                LoggerUtil.INFO("Default role found, removing from the server");
                                guild.kickUser(user, "");
                            }
                        }
                    }
                }
            }
        }, 1, 1, TimeUnit.HOURS);
        return true;
    }

    public String getName() {
        return "Temporary Membership";
    }

    public String getVersion() {
        return "1.1";
    }
}
