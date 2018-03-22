package modules.AutoRank;

import main.SpecialBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.member.UserJoinEvent;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.obj.IRole;

public class AutoRankHandler {
    private SpecialBot bot;
    private IRole rando_role;

    public AutoRankHandler(SpecialBot bot){
        this.bot = bot;
        this.rando_role = bot.getClient().getGuilds().get(0).getRolesByName("rando").get(0);
    }

    /**
     * Very simple event listener to automatically add the rando rank to anyone who joins the server
     * @param event UserJoinEvent thrown by Discord4J
     */
    @EventSubscriber
    public void onUserJoinGuild(UserJoinEvent event){
        event.getUser().addRole(rando_role);
    }


}
