package modules.AutoRole;

import main.SpecialBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.IRole;

public class AutoRoleHandler {

    private SpecialBot bot;

    public AutoRoleHandler(SpecialBot bot) {
        this.bot = bot;
    }

    /**
     * Very simple event listener to automatically add the default guild rank to every user who joins the server
     *
     * @param event UserJoinEvent thrown by Discord4J
     */
    @EventSubscriber
    public void onUserJoinGuild(UserJoinEvent event) {
        String roleId = bot.getGuildOptions(event.getGuild()).DEFAULT_ROLE;
        if (!roleId.isEmpty()) {
            IRole defaultRole = event.getClient().getRoleByID(Long.parseLong(roleId));
            event.getUser().addRole(defaultRole);
        }
    }

    @EventSubscriber
    public void onUserRoleUpdate(UserRoleUpdateEvent event) {
        String roleId = bot.getGuildOptions(event.getGuild()).DEFAULT_ROLE;
        if (!roleId.isEmpty()) {
            IRole defaultRole = event.getClient().getRoleByID(Long.parseLong(roleId));
            if (event.getOldRoles().contains(defaultRole)) {
                event.getNewRoles().remove(defaultRole);
            }
        }
    }


}
