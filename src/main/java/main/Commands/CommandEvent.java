package main.Commands;

import main.SpecialBot;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

import java.util.StringJoiner;

public class CommandEvent {

    private SpecialBot bot;
    private Command command;
    private IGuild guild;
    private IChannel channel;
    private IUser author;
    private IMessage message;
    private String label;
    private String[] args;

    public CommandEvent(SpecialBot bot, Command command, IGuild guild, IChannel channel, IUser author, IMessage message, String label, String[] args) {
        this.bot = bot;
        this.command = command;
        this.guild = guild;
        this.channel = channel;
        this.author = author;
        this.message = message;
        this.label = label;
        this.args = args;
    }

    public IGuild getGuild() {
        return guild;
    }

    public IChannel getChannel() {
        return channel;
    }

    public IUser getAuthor() {
        return author;
    }

    public IMessage getMessage() {
        return message;
    }

    public String getLabel() {
        return label;
    }

    public String[] getArgs() {
        return args;
    }

    public String getUsageMessage() {
        return "Usage: " + command.usage();
    }

    public void reply(String reply) {
        bot.sendChannelMessage(reply, channel);
    }

    public void reply(EmbedObject reply) {
        bot.sendEmbed(reply, channel);
    }

    public String getArgsAsString(int startIndex) {
        StringJoiner name = new StringJoiner(" ");
        for (int i = startIndex; i < args.length; i++) {
            name.add(args[i]);
        }
        return name.toString();
    }

    public boolean isInteger(String argument) {
        try {
            Integer.parseInt(argument);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public boolean isDouble(String argument) {
        try {
            Double.parseDouble(argument);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
