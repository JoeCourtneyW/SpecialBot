package modules.Music;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import main.JsonObjects.GuildOptions;
import main.Main;
import main.SpecialBot;
import main.SpecialModule;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IVoiceChannel;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

public class Music implements SpecialModule {
    static Music instance;

    private YouTube youtube;
    private File music_dir;
    private Downloader downloader;
    private YoutubeWrapper youtubeWrapper;
    private MusicHandler musicHandler;
    /*
            Stores the last channel that a user typed a MusicCommand into, used to reply to users on events
        */
    private ConcurrentHashMap<IGuild, SpecialAudioPlayer> audioPlayers = new ConcurrentHashMap<>();

    public boolean onLoad() {
        registerYoutube();
        youtubeWrapper = new YoutubeWrapper(this);
        downloader = new Downloader(this);
        bot.registerCommands(new MusicCommands(bot)); //Make sure commands and handlers are both at the end of the onLoad
        bot.registerHandlers((musicHandler = new MusicHandler(bot))); //method to ensure the other classes are available to them
        for (IGuild g : bot.getClient().getGuilds()) {
            audioPlayers.put(g, new SpecialAudioPlayer(bot, g));
        }
        loadGuildOptions();
        startTimeoutTimer();
        music_dir = new File(Main.DIR + File.separator + "music" + File.separator);
        instance = this;
        return true;
    }

    private void loadGuildOptions() {
        for (IGuild guild : bot.getClient().getGuilds()) {
            GuildOptions guildOptions = bot.getGuildOptions(guild);
            getAudioPlayer(guild).setVolume(guildOptions.BOT_VOLUME);
        }
    }

    private void registerYoutube() {
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), (request) -> {
        }).setApplicationName("Special Bot").build();
    }

    private void startTimeoutTimer() {//TODO: Fix
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            Stream<IVoiceChannel> voiceChannelStream;
            for (IGuild guild : bot.getClient().getGuilds()) {
                voiceChannelStream = guild.getVoiceChannels().stream();
                if (voiceChannelStream.anyMatch(IVoiceChannel::isConnected)) {
                    if (System.currentTimeMillis() - getAudioPlayer(guild).getLastAction() > 1000 * 60 * 30) {//If it's been 30 minutes since the last bot action
                        voiceChannelStream.filter(IVoiceChannel::isConnected).limit(1).findFirst().orElse(null).leave(); //Hecking cool streams dude
                    }
                }
            }
        }, 15, 15, TimeUnit.MINUTES);
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
        return "Music";
    }

    public String getVersion() {
        return "1.2";
    }
}
