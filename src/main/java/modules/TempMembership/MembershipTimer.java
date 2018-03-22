package modules.TempMembership;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import utils.LoggerUtil;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MembershipTimer extends Thread {
    private IDiscordClient client;

    public MembershipTimer(IDiscordClient client) {
        this.client = client;
    }

    public void start() {
        LoggerUtil.DEBUG("Temporary Membership thread started, checking for randos");
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
                IGuild guild = client.getGuilds().get(0);
                for (IUser user : guild.getUsers()) {
                    if (user.hasRole(guild.getRolesByName("rando").get(0))) { //they are a rando
                        LoggerUtil.INFO("Rando found, removing from the server");
                        guild.kickUser(user, "You have overstayed your welcome at The Special Church");
                        //TODO: Try to use IUser#getCreationDate to see if that shows when they joined the guild
                    }
                }
            }
        }
    }

}
