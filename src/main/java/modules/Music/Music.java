package modules.Music;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import main.JsonObjects.GuildOptions;
import main.Main;
import main.SpecialBot;
import modules.SpecialModule;
import sx.blah.discord.handle.obj.IGuild;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

public class Music extends SpecialModule {
    static Music instance;
    private String name = "Music";
    private String version = "1.1";

    private YouTube youtube;
    private File music_dir;
    private Downloader downloader;
    private YoutubeWrapper youtubeWrapper;
    private MusicHandler musicHandler;
    /*
            Stores the last channel that a user typed a MusicCommand into, used to reply to users on events
        */
    private ConcurrentHashMap<IGuild, SpecialAudioPlayer> audioPlayers = new ConcurrentHashMap<>();


    public Music(SpecialBot bot) {
        super(bot);
        music_dir = new File(Main.DIR + File.separator + "music" + File.separator);
        instance = this;
    }

    public boolean enable() {
        registerYoutube();
        youtubeWrapper = new YoutubeWrapper(this);
        downloader = new Downloader(this);
        registerCommands(new MusicCommands(bot)); //Make sure commands and handlers are both at the end of the enable
        registerHandlers((musicHandler = new MusicHandler(bot))); //method to ensure the other classes are available to them
        for (IGuild g : bot.getClient().getGuilds()) {
            audioPlayers.put(g, new SpecialAudioPlayer(bot, g));
        }
        loadGuildOptions();
        return true;
    }

    private void loadGuildOptions() {
        for (IGuild guild : bot.getClient().getGuilds()) {
            GuildOptions guildOptions = bot.getGuildOptions(guild);
            getAudioPlayer(guild).setVolume(guildOptions.BOT_VOLUME);
        }
    }

    private void registerYoutube() {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
            }
        }).setApplicationName("Special Bot").build();
    }

    public SpecialAudioPlayer getAudioPlayer(IGuild guild) {
        return audioPlayers.get(guild);
    }

    public SpecialBot getBot() {
        return bot;
    }

    public YouTube getYoutube() {
        return youtube;
    }

    public YoutubeWrapper getYoutubeWrapper() {
        return youtubeWrapper;
    }

    public Downloader getDownloader() {
        return downloader;
    }

    public MusicHandler getMusicHandler() {
        return musicHandler;
    }

    public File getMusicDirectory() {
        return music_dir;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
