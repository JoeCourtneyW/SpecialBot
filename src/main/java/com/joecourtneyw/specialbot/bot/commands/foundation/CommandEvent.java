package com.joecourtneyw.specialbot.bot.commands.foundation;


import com.joecourtneyw.specialbot.bot.SpecialBot;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.TextChannel;
import discord4j.core.object.entity.User;

import java.util.StringJoiner;

public class CommandEvent {

    private SpecialBot bot;
    private Command command;
    private Guild guild;
    private TextChannel channel;
    private User author;
    private Message message;
    private String label;
    private String[] args;

    public CommandEvent(SpecialBot bot, Command command, Guild guild, TextChannel channel, User author, Message message, String label, String[] args) {
        this.bot = bot;
        this.command = command;
        this.guild = guild;
        this.channel = channel;
        this.author = author;
        this.message = message;
        this.label = label;
        this.args = args;
    }

    public Guild getGuild() {
        return guild;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public User getAuthor() {
        return author;
    }

    public Message getMessage() {
        return message;
    }

    public String getLabel() {
        return label;
    }

    public String[] getArgs() {
        return args;
    }

    public String getUsageMessage() {
        return "*Usage: " + command.usage() + "*";
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
