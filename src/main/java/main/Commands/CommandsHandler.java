package main.Commands;

import main.SpecialBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.Permissions;
import utils.AnnotationUtil;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;

public class CommandsHandler {

    private SpecialBot bot;
    private HashMap<Command, Method> commands = new HashMap<>();

    public CommandsHandler(SpecialBot bot) {
        this.bot = bot;
    }

    public void registerCommand(CommandExecutor executor) {
        for (Method m : AnnotationUtil.getAnnotatedMethods(executor.getClass(), Command.class)) {
            commands.put((Command) AnnotationUtil.getAnnotation(m, Command.class), m);
        }
    }

    @EventSubscriber
    public void handleCommands(MessageReceivedEvent event) {
        String prefix = ".";
        if (!bot.guildOptionsExist(
                event.getGuild())) { //This guild has not yet been set up, don't let them run any command except .setup
            if (!event.getMessage().getContent().startsWith(".setup")) { //Make sure they run .setup
                bot.sendChannelMessage("***You must first run .setup to be able to use commands on this server***",
                                       event.getChannel());
                return;
            } //If they run .setup, let the prefix stay as '.' and the command should run as normally expected
        } else {
            prefix = bot.getGuildOptions(event.getGuild()).PREFIX;
        }
        IMessage message = event.getMessage();

        if (!message.getContent().startsWith(prefix))
            return;

        if (message.getContent().equalsIgnoreCase(prefix)) {
            bot.sendChannelMessage("*Type '" + prefix + "help' to show a list of commands" + "*",
                                   message.getChannel());
            return;
        }
        String[] split = message.getContent().split(" ");

        String commandLabel = split[0].substring(prefix.length());
        String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];

        for (Command ca : commands.keySet()) {
            if (ca.label().equalsIgnoreCase(commandLabel) || ca.alias().equalsIgnoreCase(commandLabel)) {
                if (!ca.adminOnly() || (ca.adminOnly() && event.getAuthor().getPermissionsForGuild(
                        event.getGuild()).contains(
                        Permissions.ADMINISTRATOR))) { //If the command is for everyone or the command is adminOnly and the user is an admin in the given guild
                    try {
                        Method commandMethod = commands.get(ca);
                        commandMethod.setAccessible(true);
                        CommandExecutor declaringClassInstance = (CommandExecutor) commandMethod.getDeclaringClass()
                                .getConstructor().newInstance(); //Creates new instance of commandexecutor class with required constructor
                        commandMethod.invoke(declaringClassInstance,
                                             new CommandEvent(bot, ca,//Invokes command method with CommandEvent arg
                                                              event.getGuild(),
                                                              event.getChannel(),
                                                              event.getAuthor(),
                                                              event.getMessage(),
                                                              commandLabel, args));
                    } catch (Exception e) {
                        bot.sendChannelMessage("*An unexpected error has occured*",
                                               message.getChannel());
                        e.printStackTrace();
                    }
                } else {
                    bot.sendChannelMessage("*You don't have permission to execute this command!*",
                                           message.getChannel());
                }
                return;
            }

        }
        bot.sendChannelMessage("*Type '" + prefix + "help' to show a list of commands*",
                               message.getChannel());
    }
}
