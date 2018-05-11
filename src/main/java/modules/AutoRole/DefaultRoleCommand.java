package modules.AutoRole;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.JsonObjects.GuildOptions;
import main.SpecialBot;
import sx.blah.discord.handle.obj.IRole;

import java.util.List;

public class DefaultRoleCommand extends CommandExecutor {
    public DefaultRoleCommand(SpecialBot bot) {
        super(bot);
    }

    @Command(label="defaultrole", adminOnly = true, usage = ".defaultrole [Role Mention]", description = "Changes the default role of the server")
    public void onDefaultRole(CommandEvent event){
        List<IRole> mentions = event.getMessage().getRoleMentions();
        if(mentions.size() != 1){
            event.reply(event.getUsageMessage());

        }
        GuildOptions options = bot.getGuildOptions(event.getGuild());
        options.DEFAULT_ROLE = mentions.get(0).getStringID();
        bot.updateGuildOptions(options);
    }
}
