package modules.AutoRole;

import main.SpecialModule;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.IRole;

public class AutoRole implements SpecialModule {

    public boolean onLoad() {
        bot.registerHandlers(this);
        return true;
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
                event.getUser().removeRole(defaultRole);
            }
        }
    }

    public String getName() {
        return "AutoRole";
    }

    public String getVersion() {
        return "1.1";
    } //Finally 1.1 fixes the remove default role on update bug
}
