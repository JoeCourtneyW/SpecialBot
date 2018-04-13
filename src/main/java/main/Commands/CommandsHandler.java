package main.Commands;

import main.SpecialBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.guild.channel.message.MessageReceivedEvent;
import sx.blah.discord.handle.obj.IMessage;
import utils.AnnotationUtil;

import java.lang.reflect.Method;
import java.util.HashMap;

public class CommandsHandler {
    private SpecialBot bot;
    private String PREFIX = "."; //TODO: Make this guild specific
    private HashMap<Command, Method> commands = new HashMap<>();

    public CommandsHandler(SpecialBot bot){
        this.bot = bot;
    }

    public void registerCommand(CommandExecutor executor) {
        for (Method m :  AnnotationUtil.getAnnotatedMethods(executor.getClass(), Command.class)) {
            commands.put((Command) AnnotationUtil.getAnnotation(m, Command.class), m);
        }
    }

    @EventSubscriber
    public void handleCommands(MessageReceivedEvent event) {
        IMessage message = event.getMessage();
        if (!message.getContent().startsWith(PREFIX))
            return;
        if (message.getContent().equalsIgnoreCase(PREFIX)) {
            bot.sendChannelMessage("Type '" + PREFIX + "help' to show a list of commands",
                    message.getChannel());
            return;
        }
        String commandLabel = message.getContent().split(" ")[0].substring(PREFIX.length());
        for (Command ca : commands.keySet()) {
            if (ca.label().equalsIgnoreCase(commandLabel) || ca.alias().equalsIgnoreCase(commandLabel)) {
                if(true){ //TODO: PERMISSION CHECK
                    try {
                        Method commandMethod = commands.get(ca);
                        commandMethod.setAccessible(true);
                        CommandExecutor declaringClassInstance = (CommandExecutor) commandMethod.getDeclaringClass().getConstructor(SpecialBot.class).newInstance(bot);

                        commandMethod.invoke(declaringClassInstance, message);
                    } catch (Exception e) {
                        bot.sendChannelMessage("An error occured in the execution of the command",
                                message.getChannel());
                        e.printStackTrace();
                    }
                }else{
                    bot.sendChannelMessage("You don't have permission to execute this command!", message.getChannel());
                }
                return;
            }

        }
        bot.sendChannelMessage("Type '" + PREFIX + "help' to show a list of commands",
                message.getChannel());
    }
}
