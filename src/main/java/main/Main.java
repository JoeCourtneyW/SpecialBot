package main;

import modules.AutoRole.AutoRole;
import modules.Miscellaneous.Miscellaneous;
import modules.Music.Music;
import modules.Reddit.Reddit;
import modules.Steam.Steam;
import modules.TempMembership.TempMembership;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import utils.JsonUtil;
import utils.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class Main {
    public static Credentials CREDENTIALS;
    public final static String DIR = System.getProperty("user.dir");

    public static SpecialBot bot;

    public static void main(String[] args) {
        String credentialsFile = "credentials.json";
        if (args.length > 0)
            credentialsFile = args[0];

        CREDENTIALS = loadCredentials(new File(credentialsFile));
        if (CREDENTIALS == null) {
            LoggerUtil.FATAL("Failed to load credentials file");
            return;
        }

        bot = login();
        bot.getClient().getDispatcher().registerListener(new Main()); //Waits for ready event to initialize modules and such
        bot.registerCommands(new CommandUpdate(bot)); //Leave the command update while the bot is still developing
    }

    private static SpecialBot login() {
        return new SpecialBot(createClient(CREDENTIALS.PERSONAL_TOKEN));
    }


    private static IDiscordClient createClient(String token) { // Returns a new instance of the Discord client
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token); // Uses the given token for the client
        try {
            return clientBuilder.login();
        } catch (DiscordException e) {
            e.printStackTrace(); //There was some error during the login, most likely with the token
        }
        return null;
    }

    private static void activateModules() {
        bot.loadModule(new AutoRole());
        bot.loadModule(new TempMembership());
        bot.loadModule(new Reddit());
        bot.loadModule(new Steam());
        bot.loadModule(new Music());
        bot.loadModule(new Miscellaneous());
    }

    /**
     * @param credentialsFile the JSON file that contains the credentials
     * @return A Credentials object
     */
    private static Credentials loadCredentials(File credentialsFile) {
        return (Credentials) JsonUtil.getJavaObject(credentialsFile, Credentials.class);
    }

    public static String getProjectVersion() {
        String version = "";
        try {
            Properties botProperties = new Properties();
            botProperties.load(ClassLoader.getSystemResourceAsStream("project.properties"));
            version = botProperties.getProperty("ver");
        } catch (IOException e) {
            LoggerUtil.FATAL("Failed to load bot properties file from resources");
            e.printStackTrace();
            System.exit(0);
        }
        return version;
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        bot.setupClient(CREDENTIALS.CLIENT_ID);
        activateModules();
        LoggerUtil.PLAIN("");
        LoggerUtil.INFO("SpecialBot started... Listening for user commands...");
    }
}
