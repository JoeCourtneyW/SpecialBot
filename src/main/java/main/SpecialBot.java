package main;

import main.Commands.CommandsHandler;
import modules.SpecialModule;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import utils.LoggerUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class SpecialBot {

    private IDiscordClient client;
    private List<SpecialModule> modules;
    private CommandsHandler commandsHandler;
    private ExecutorService asyncExecutor;

    public SpecialBot(IDiscordClient client) {
        this.client = client;
        modules = new ArrayList<>();
        this.asyncExecutor = Executors.newFixedThreadPool(2);
        registerCommandsHandler();
    }

    public IDiscordClient getClient() {
        return client;
    }

    public ExecutorService getAsyncExecutor() {
        return this.asyncExecutor;
    }

    private void registerCommandsHandler() {
        this.commandsHandler = new CommandsHandler(this);
        getClient().getDispatcher().registerListener(commandsHandler);
    }

    public CommandsHandler getCommandsHandler() {
        return this.commandsHandler;
    }

    public List<SpecialModule> getModules() {
        return modules;
    }

    public void addModule(SpecialModule module) {
        if (enableModule(module))
            modules.add(module);
    }

    private boolean enableModule(SpecialModule module) {
        if (module.enable()) {
            LoggerUtil.INFO("[Module] Enabled \"" + module.getName() + "\" V" + module.getVersion());
            return true;
        } else {
            LoggerUtil.WARNING("[Module] \"" + module.getName() + "\" has failed to load.");
            return false;
        }
    }

    public void setupClient(String client_id) {
        if (client.getGuilds().size() == 0) {
            LoggerUtil.CRITICAL("You need to add this bot to a server. Use the link below:");
            LoggerUtil.INFO("https://discordapp.com/api/oauth2/authorize?client_id=" + client_id + "&scope=bot");
        }
        try {
            client.changeUsername("Special Boi");
            client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, "SNAPSHOT-1.0.0");
            //Image i = Image.defaultAvatar(); //TODO
            //INSTANCE.client.changeAvatar(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IMessage sendMessage(MessageBuilder builder) {
        try {
            return builder.build();
        } catch (RateLimitException e) {
            LoggerUtil.CRITICAL("Slow down! The bot is sending messages too quickly!");
        } catch (DiscordException e) {
            LoggerUtil.CRITICAL("Discord Exception: " + e.getErrorMessage());
        } catch (MissingPermissionsException e) {
            LoggerUtil.CRITICAL("The bot does not have permission to chat in this channel! [#" + builder.getChannel().getName() + "]");
        }
        return null;
    }

    public IMessage sendChannelMessage(String message, IChannel channel) {
        return sendMessage(new MessageBuilder(client)
                .appendContent(message)
                .withChannel(channel));
    }

    public IMessage sendFile(String message, File image, IChannel channel) {
        try {
            return sendMessage(new MessageBuilder(client)
                    .withContent(message)
                    .withFile(image)
                    .withChannel(channel));
        } catch (FileNotFoundException e) {
            LoggerUtil.CRITICAL("That file does not exist!");
        }
        return null;
    }

    public IMessage sendEmbed(EmbedObject embed, IChannel channel) {
        return sendMessage(new MessageBuilder(client)
                .withEmbed(embed)
                .withChannel(channel));
    }

    public IMessage sendPrivateMessage(String message, IUser user) {
        IPrivateChannel pc = client.getOrCreatePMChannel(user);
        return sendMessage(new MessageBuilder(client)
                .appendContent(message)
                .withChannel(pc));
    }

    /*Format options for chat messages, just for reference
        ITALICS("*"),
		BOLD("**"),
		BOLD_ITALICS("***"),
		STRIKEOUT("~~"),
		UNDERLINE("__"),
		UNDERLINE_ITALICS("__*"),
		UNDERLINE_BOLD("__**"),
		UNDERLINE_BOLD_ITALICS("__***"),
		CODE_BLOCK("`"),
		MULTI_LINE_CODE_BLOCK("```");
     */
    public boolean tryDiscordFunction(Runnable runnable) {
        try {
            runnable.run();
            return true;
        } catch (RateLimitException e) {
            LoggerUtil.CRITICAL("Slow down! The bot is attempting actions too quickly!" + " Please wait " + e.getRetryDelay() + "ms");
        } catch (DiscordException e) {
            LoggerUtil.CRITICAL("Discord Exception: " + e.getErrorMessage());
        } catch (MissingPermissionsException e) {
            LoggerUtil.CRITICAL("The bot is missing permissions for this action" + e.getMissingPermissions().toString());
        }
        return false;
    }
}
