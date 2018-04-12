package main;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import modules.AutoRank.AutoRank;
import modules.Music.Music;
import modules.Reddit.Reddit;
import modules.Steam.CommandSearch;
import modules.Steam.Steam;
import modules.TempMembership.TempMembership;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import utils.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Scanner;

public class Main {
    public static HashMap<String, JsonNode> CREDENTIALS;
    public final static String DIR = System.getProperty("user.dir");

    public static SpecialBot bot;

    public static void main(String[] args){
        String credentialsFile = "credentials.json";
        if (args.length > 0)
            credentialsFile = args[0];

        CREDENTIALS = loadCredentials(new File(credentialsFile));
        if (CREDENTIALS == null)
            return;
        bot = login();
        bot.getClient().getDispatcher().registerListener(new Main()); //Waits for ready event to initialize modules and such

        test_code();

        Scanner control = new Scanner(System.in);
        if (control.nextLine().equalsIgnoreCase("exit")) {
            bot.getClient().logout();
            System.exit(0);
        }
    }

    private static SpecialBot login() {
        return new SpecialBot(createClient(CREDENTIALS.get("PERSONAL_TOKEN").asText()));
    }

    private static void activateModules() {
        bot.addModule(new AutoRank(bot));
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
     * @return A HashMap with stirng keys, and JsonNodes for the credentials
     */
    private static HashMap<String, JsonNode> loadCredentials(File credentialsFile) {
        HashMap<String, JsonNode> credentials = new HashMap<>();
        try {
            byte[] jsonData = Files.readAllBytes(credentialsFile.toPath());
            ObjectMapper objectMapper = new ObjectMapper();
            credentials.put("PERSONAL_TOKEN", objectMapper.readTree(jsonData).path("PERSONAL_TOKEN"));
            credentials.put("CLIENT_ID", objectMapper.readTree(jsonData).path("CLIENT_ID"));
            credentials.put("REDDIT_USER", objectMapper.readTree(jsonData).path("REDDIT_USER"));
            credentials.put("REDDIT_PASSWORD", objectMapper.readTree(jsonData).path("REDDIT_PASSWORD"));
            credentials.put("REDDIT_CLIENT_ID", objectMapper.readTree(jsonData).path("REDDIT_CLIENT_ID"));
            credentials.put("REDDIT_SECRET_KEY", objectMapper.readTree(jsonData).path("REDDIT_SECRET_KEY"));
            credentials.put("GOOGLE_API_KEY", objectMapper.readTree(jsonData).path("GOOGLE_API_KEY"));
        } catch (IOException ioe) {
            LoggerUtil.CRITICAL("Credentials file failed to load, cancelling startup.");
            return null;
        }
        return credentials;
    }

    @EventSubscriber
    public void onReady(ReadyEvent event) {
        bot.setupClient(CREDENTIALS.get("CLIENT_ID").asText());
        activateModules();
    }

    private static void test_code() {
        //Place test code in this block and it will be run on startup
    }
}
