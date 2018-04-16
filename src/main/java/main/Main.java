package main;

import main.JsonObjects.Credentials;
import modules.AutoRole.AutoRole;
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
        bot.getCommandsHandler().registerCommand(new CommandUpdate(bot));
    }

    private static SpecialBot login() {
        return new SpecialBot(createClient(CREDENTIALS.PERSONAL_TOKEN));
    }

    private static void activateModules() {
        bot.addModule(new AutoRole(bot));
        bot.addModule(new TempMembership(bot));
        bot.addModule(new Reddit(bot));
        bot.addModule(new Steam(bot));
        bot.addModule(new Music(bot));
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

    /**
     * @param credentialsFile the JSON file that contains the credentials
     * @return A Credentials object
     */
    private static Credentials loadCredentials(File credentialsFile) {
        return (Credentials) JsonUtil.getJavaObject(credentialsFile, Credentials.class);
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        bot.setupClient(CREDENTIALS.CLIENT_ID);
        activateModules();
    }
}
