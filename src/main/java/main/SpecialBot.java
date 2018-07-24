package main;

import main.Commands.CommandExecutor;
import main.Commands.CommandsHandler;
import main.GuildOptions.GuildOptions;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;
import utils.JsonUtil;
import utils.LoggerUtil;

import java.io.File;
import java.io.IOException;

public class SpecialBot {

    private IDiscordClient client;
    private CommandsHandler commandsHandler;
    private String GUILD_OPTIONS_DIR;

    public SpecialBot(IDiscordClient client) {
        this.client = client;
        registerCommandsHandler();
    }

    public void setupClient(String client_id) {
        setupGuildOptions();
        updatePresence();

        if (client.getGuilds().size() == 0) {
            LoggerUtil.CRITICAL("You need to add this bot to a server. Use the link below:");
            LoggerUtil.INFO("https://discordapp.com/api/oauth2/authorize?client_id=" + client_id + "&scope=bot");
            System.exit(0);
            return;
        }

    }

    public IDiscordClient getClient() {
        return client;
    }

    private void registerCommandsHandler() {
        this.commandsHandler = new CommandsHandler(this);
        getClient().getDispatcher().registerListener(commandsHandler);
    }

    public void loadModule(SpecialModule module) {
        if (module.onLoad()) {
            LoggerUtil.INFO("[Modules] Enabled \"" + module.getName() + "\" V" + module.getVersion());
        } else {
            LoggerUtil.WARNING("[Modules] \"" + module.getName() + "\" has failed to load.");
        }
    }

    public void registerHandlers(Object... handlers) {
        for (Object handler : handlers)
            client.getDispatcher().registerListener(handler);
    }

    public void registerCommands(CommandExecutor... executors) {
        for (CommandExecutor executor : executors)
            commandsHandler.registerCommand(executor);
    }

    private void updatePresence() {
        String version = Main.getProjectVersion();

        client.changeUsername("Special Boi");
        client.changePresence(StatusType.ONLINE, ActivityType.PLAYING, version);

        try {
            File avatar = new File(Main.DIR + File.separator + "avatar.png");
            if (avatar.exists()) {
                Image img = Image.forFile(avatar);
                client.changeAvatar(img);
            } else {
                LoggerUtil.WARNING("Failed to load avatar image for discord user: No avatar.png file found");
            }
        } catch (DiscordException e) {
            LoggerUtil.WARNING("Failed to update avatar, rate limited by discord");
        }
    }

    public boolean joinVoiceChannel(IChannel channel) {
        IVoiceChannel voiceChannel = channel.getGuild().getVoiceChannelByID(
                channel.getLongID()); //Convert IChannel given by user into a voice channel
        if (!voiceChannel.getModifiedPermissions(getClient().getOurUser()).contains(Permissions.VOICE_CONNECT))
            return false;
        else if (voiceChannel.getConnectedUsers().size() >= voiceChannel.getUserLimit() && voiceChannel.getUserLimit() > 0)
            return false;
        else {
            voiceChannel.join();
            return true;
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
            LoggerUtil.CRITICAL(
                    "The bot does not have permission to chat in this channel! [#" + builder.getChannel().getName() + "]");
        }
        return null;
    }

    public IMessage sendChannelMessage(String message, IChannel channel) {
        return sendMessage(new MessageBuilder(client)
                                   .appendContent(message)
                                   .withChannel(channel));
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


    private void setupGuildOptions() {
        GUILD_OPTIONS_DIR = Main.DIR + File.separator + "guild_options";
        File guildOptionsDirFile = new File(GUILD_OPTIONS_DIR);
        if (!guildOptionsDirFile.exists()) {
            guildOptionsDirFile.mkdir();
        }
        for (IGuild guild : client.getGuilds()) {
            if (!guildOptionsExist(guild)) {
                createNewGuildOptions(guild);
            }
        }
    }

    public boolean guildOptionsExist(IGuild guild) {
        return new File(GUILD_OPTIONS_DIR + File.separator + guild.getStringID() + ".json").exists();
    }

    public void createNewGuildOptions(IGuild guild) {
        try {
            File optionsFile = new File(GUILD_OPTIONS_DIR + File.separator + guild.getStringID() + ".json");
            optionsFile.createNewFile();
            GuildOptions newGuild = new GuildOptions();
            newGuild.GUILD_ID = guild.getStringID();
            JsonUtil.updateJsonFile(optionsFile, newGuild);
        } catch (IOException e) {
            LoggerUtil.CRITICAL("Failed to create new guild file while setting up guild options");
        }

    }

    public void updateGuildOptions(GuildOptions guildOptions) {
        File optionsFile = new File(GUILD_OPTIONS_DIR + File.separator + guildOptions.GUILD_ID + ".json");
        JsonUtil.updateJsonFile(optionsFile, guildOptions);
    }

    public GuildOptions getGuildOptions(IGuild guild) {
        return (GuildOptions) JsonUtil.getJavaObject(
                new File(GUILD_OPTIONS_DIR + File.separator + guild.getStringID() + ".json"), GuildOptions.class);
    }
}
