import discord.Main;
import discord.modules.audio.Audio;
import discord.modules.command.Commands;
import discord.modules.config.Configuration;
import discord.modules.database.Database;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.obj.ActivityType;
import sx.blah.discord.handle.obj.Status;
import sx.blah.discord.handle.obj.StatusType;
import sx.blah.discord.util.DiscordException;

public class SpecialBot {
    //https://discordapp.com/api/oauth2/authorize?client_id=273659666054119425&scope=bot
    private final static String PERSONAL_TOKEN = "MjczNjU5NjY2MDU0MTE5NDI1.C2mw-Q.5lI1_lHcWWx5oRfHPQtGcVV4tnA";
    private final static String CLIENT_ID = "273659666054119425";
    private final static String SERVER_ADD_LINK = "https://discordapp.com/api/oauth2/authorize?client_id=" + CLIENT_ID + "&scope=bot";

    public static String PREFIX = "!";
    public static String ADMIN_ROLE = "BotAdmin";
    public static String MODERATOR_ROLE = "BotMod";
    public static String DATABASE_USERNAME = "root";
    public static String DATABASE_PASSWORD = "password";

    public static SpecialBot INSTANCE;
    public IDiscordClient client;
    public static Commands commandsModule;
    public static Configuration configModule;
    public static Audio audioModule;
    public static Database databaseModule;

    public static void main( String[] args )
    {
        INSTANCE = login();
        EventDispatcher dispatcher = INSTANCE.client.getDispatcher();
        dispatcher.registerListener(INSTANCE);
        activateModules();
    }

    public SpecialBot(IDiscordClient dclient){
        this.client = dclient;
    }
    private static SpecialBot login(){
        return new SpecialBot(createClient(PERSONAL_TOKEN, true));
    }

    private static void activateModules(){
        commandsModule = new Commands();
        commandsModule.enable(INSTANCE.client);
        configModule = new Configuration();
        configModule.enable(INSTANCE.client);
        audioModule = new Audio();
        audioModule.enable(INSTANCE.client);
        databaseModule = new Database();
        databaseModule.enable(INSTANCE.client);
    }

    public static IDiscordClient createClient(String token, boolean login) { // Returns a new instance of the Discord client
        ClientBuilder clientBuilder = new ClientBuilder(); // Creates the ClientBuilder instance
        clientBuilder.withToken(token); // Adds the login info to the builder
        try {
            if (login) {
                return clientBuilder.login(); // Creates the client instance and logs the client in
            } else {
                return clientBuilder.build(); // Creates the client instance but it doesn't log the client in yet, you would have to call client.login() yourself
            }
        } catch (DiscordException e) { // This is thrown if there was a problem building the client
            e.printStackTrace();
            return null;
        }

    }
    @EventSubscriber
    public void onReady(ReadyEvent event){
        if(INSTANCE.client.getGuilds().size() == 0){
            System.out.println("Looks like you haven't added the bot to a server! Click this link to register him.");
            System.out.println(SERVER_ADD_LINK);
        }
        try {
            INSTANCE.client.changeUsername("Special Boi");
            INSTANCE.client.changePresence(StatusType.ONLINE, ActivityType.STREAMING, "SNAPSHOT-1.0.0");
            //Image i = Image.defaultAvatar(); //TODO
            //INSTANCE.client.changeAvatar(i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PREFIX = configModule.getConfigValues().get("PREFIX");
        ADMIN_ROLE = configModule.getConfigValues().get("ADMIN_ROLE");
        MODERATOR_ROLE = configModule.getConfigValues().get("MODERATOR_ROLE");
        DATABASE_USERNAME = configModule.getConfigValues().get("DATABASE_USERNAME");
        DATABASE_PASSWORD = configModule.getConfigValues().get("DATABASE_PASSWORD");
        databaseModule.load();
    }
}
