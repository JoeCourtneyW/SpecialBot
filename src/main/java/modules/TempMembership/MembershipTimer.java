package modules.TempMembership;

import main.JsonObjects.GuildOptions;
import main.SpecialBot;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import utils.LoggerUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MembershipTimer extends Thread {
    private SpecialBot bot;

    public MembershipTimer(SpecialBot bot) {
        this.bot = bot;
    }

    public void start() {
        while (true) {
            try {
                Thread.sleep(1000 * 60 * 55);
            } catch (InterruptedException e) {
                LoggerUtil.CRITICAL("Temporary Membership thread interrupted, restarting thread");
                start();
                return;
            }
            DateFormat dateFormat = new SimpleDateFormat("HH");
            Date now = new Date();
            String hour = dateFormat.format(now);
            if (Integer.valueOf(hour) == 6) {
                //It's part of the 6AM hour, start kicking members who have overstayed their welcome
                for (IGuild guild : bot.getClient().getGuilds()) {
                    GuildOptions guildOptions = bot.getGuildOptions(guild);
                    if (guildOptions.AUTO_KICK) {
                        for (IUser user : guild.getUsers()) {
                            if (!guildOptions.DEFAULT_ROLE.isEmpty()) {
                                if (user.hasRole(guild.getRoleByID(Long.parseLong(guildOptions.DEFAULT_ROLE)))) { //they are still default role
                                    LoggerUtil.INFO("Default role found, removing from the server");
                                    guild.kickUser(user, "You have overstayed your welcome");
                                    //TODO: Try to use IUser#getCreationDate to see if that shows when they joined the guild
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
