package main.Commands;

import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class CommandEvent {
    private Command command;
    private IGuild guild;
    private IChannel channel;
    private IUser author;
    private IMessage message;
    private String label;
    private String[] args;

    public CommandEvent(Command command, IGuild guild, IChannel channel, IUser author, IMessage message, String label, String[] args) {
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
    public String getUsageMessage(){
        return "Usage: " + command.usage();
    }
    public void sendNoPermission(){
        //TODO
    }
}