package main.GuildOptions;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.PermissionUtils;

public class GuildOptionsCommands implements CommandExecutor {

    @Command(label = "prefix", description = "Change the prefix for your guild", guildAdminOnly = true)
    public void prefix(CommandEvent event) {
        GuildOptions options = bot.getGuildOptions(event.getGuild());

        if (event.getArgs().length == 0) {
            event.reply("**Your guild's prefix is '" + options.PREFIX + "'**");
        } else if (event.getArgs().length == 1) {
            event.reply("**Your guild's new prefix is '" + event.getArgs()[0] + "'**");
            options.PREFIX = event.getArgs()[0];
            bot.updateGuildOptions(options);
        } else {
            event.reply("*Try " + options.PREFIX + "prefix [Desired Prefix]*");
        }
    }

    @Command(label = "defaultRole", description = "Change the default role for your guild", guildAdminOnly = true)
    public void defaultRole(CommandEvent event) {
        GuildOptions options = bot.getGuildOptions(event.getGuild());
        if (event.getArgs().length == 0) {
            if (options.DEFAULT_ROLE != null && !options.DEFAULT_ROLE.isEmpty())
                event.reply("**Your guild's default role is " + event.getGuild().getRoleByID(
                        Long.parseLong(options.DEFAULT_ROLE)).mention() + "**");
            else
                event.reply(
                        "*Your guild does not have a default role, set one with " + options.PREFIX + "defaultRole [Default Role Mention]*");
        } else if (event.getArgs().length == 1) {
            if (event.getMessage().getRoleMentions().size() < 1) {
                event.reply("*The first argument must be a mention of the role you wish to set*");
            } else {
                event.reply(
                        "**Your guild's new default role is " + event.getMessage().getRoleMentions().get(0).mention());
                options.DEFAULT_ROLE = event.getMessage().getRoleMentions().get(0).getStringID();
                bot.updateGuildOptions(options);
            }
        } else {
            event.reply("*Try " + options.PREFIX + "defaultRole [Default Role Mention]*");
        }
    }

    @Command(label = "rolePermission", description = "Set the permission level's corresponding roles", alias = "rolePerm", guildAdminOnly = true)
    public void rolePermission(CommandEvent event) {
        GuildOptions options = bot.getGuildOptions(event.getGuild());
        if (event.getArgs().length <= 1) {
            event.reply(
                    "Usage: *" + options.PREFIX + "rolePermission [Permission Level] [Role Mention (or NONE to reset)]*" + "\n"
                            + "Accepted Permission Levels: *(MEMBER, PRIVILIGED_MEMBER, MOD, ADMIN)*");
        } else if (event.getArgs().length == 2) {

            if (event.getMessage().getRoleMentions().size() < 1) {
                if (!event.getArgs()[1].equalsIgnoreCase("none")) {
                    event.reply("*Your second argument must be a mention of the role you wish to set or type 'NONE' to reset the permission level*");
                    return;
                }

                boolean switchResult = setPermissionLevelRole(options, event.getArgs()[0], "");
                if(!switchResult){
                    event.reply("*Accepted Permission Levels: (MEMBER, PRIVILIGED_MEMBER, MOD, ADMIN)*");
                    return;
                }

                event.reply(
                        "**The permission level** ***" + event.getArgs()[0].toUpperCase() + "*** **has been reset**");
                bot.updateGuildOptions(options);
                return;
            }

            IRole role = event.getMessage().getRoleMentions().get(0);

            boolean switchResult = setPermissionLevelRole(options, event.getArgs()[0], role.getStringID());
            if(!switchResult){
                event.reply("*Accepted Permission Levels: (MEMBER, PRIVILIGED_MEMBER, MOD, ADMIN)*");
                return;
            }

            event.reply(
                    "**The permission level** ***" + event.getArgs()[0].toUpperCase() + "*** **now corresponds to the role " + role.mention() + "**");
            bot.updateGuildOptions(options);

        } else {
            event.reply(
                    "Usage: *" + options.PREFIX + "rolePermission [Permission Level] [Role Mention (or NONE to reset)]*" + "\n"
                            + "*Permission Levels: (MEMBER, PRIVILIGED_MEMBER, MOD, ADMIN)*");
        }
    }

    private boolean setPermissionLevelRole(GuildOptions options, String permissionLevel, String roleId){
        switch (permissionLevel.toLowerCase()) {
            case "member":
                options.MEMBER_ROLE = roleId;
                break;
            case "priviliged_member":
                options.PRIVILIGED_MEMBER_ROLE = roleId;
                break;
            case "mod":
                options.MODERATOR_ROLE = roleId;
                break;
            case "admin":
                options.ADMIN_ROLE = roleId;
                break;
            default:

                return false;
        }
        return true;
    }

    @Command(label = "tempMembership", description = "Toggle the temporary membership feature for your guild", guildAdminOnly = true)
    public void tempMembership(CommandEvent event) {
        GuildOptions options = bot.getGuildOptions(event.getGuild());

        if (event.getArgs().length == 0) {
            event.reply(
                    "**Your guild currently has temporary membership " + (options.AUTO_KICK ? "enabled" : "disabled") + "**");
        } else if (event.getArgs().length == 1) {
            if (options.DEFAULT_ROLE == null || options.DEFAULT_ROLE.isEmpty()) {
                event.reply("*You must first set the guild's default role before you can enable temporary membership*");
                event.reply("*Try using " + options.PREFIX + "defaultRole [Default Role Mention]");
                return;
            }
            if (event.getArgs()[0].equalsIgnoreCase("on") || event.getArgs()[0].equalsIgnoreCase(
                    "true") || event.getArgs()[0].equalsIgnoreCase("yes") || event.getArgs()[0].equalsIgnoreCase(
                    "y") || event.getArgs()[0].equalsIgnoreCase("enable")) {
                event.reply("**You have enabled temporary membership for your guild.");
                event.reply(
                        "***Users with the default role will be removed from the guild 24 hours from their joining***");
            } else if (event.getArgs()[0].equalsIgnoreCase("off") || event.getArgs()[0].equalsIgnoreCase(
                    "false") || event.getArgs()[0].equalsIgnoreCase("no") || event.getArgs()[0].equalsIgnoreCase(
                    "n") || event.getArgs()[0].equalsIgnoreCase("disable")) {
                event.reply("**You have disabled temporary membership for your guild.");
                event.reply(
                        "***Users with the default role will NO LONGER be removed from the guild 24 hours from their joining***");
            } else {
                event.reply("*Try " + options.PREFIX + "tempMembership [off, on]*");
            }
        } else {
            event.reply("*Try " + options.PREFIX + "tempMembership [off, on]*");
        }
    }


    @Command(label = "setup", description = "Initial command to set up the bot's features for your guild", guildAdminOnly = true)
    public void setup(CommandEvent event) {
        if (bot.guildOptionsExist(event.getGuild())) {
            event.reply("*You have already set up the bot's features for your guild!*");
            return;
        } else {
            event.reply("Beginning setup...");
            if (PermissionUtils.hasPermissions(event.getGuild(), bot.getClient().getOurUser(),
                    Permissions.MANAGE_ROLES)) {
                //TODO: Verify exactly which permissions we need to have to set up the bot, and what permissions we need in our 'bot' role
            }
        }
    }

}
