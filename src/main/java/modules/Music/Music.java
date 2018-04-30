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

public class Music extends SpecialModule {
    static Music instance;
    private String name = "Music";
    private String version = "1.1";

    private YouTube youtube;
    private AudioManager audioManager;
    private File music_dir;
    private Downloader downloader;
    private YoutubeWrapper youtubeWrapper;

    public Music(SpecialBot bot){
        super(bot);
        music_dir = new File(Main.DIR + File.separator + "music" + File.separator);
        instance = this;
    }

    public boolean enable() {
        registerYoutube();
        youtubeWrapper = new YoutubeWrapper(this);
        audioManager = new AudioManager(this);
        downloader = new Downloader(this);
        registerCommands(new MusicCommands(bot)); //Make sure commands and handlers are both at the end of the enable
        registerHandlers(new MusicHandler(bot)); //method to ensure the other classes are available to them
        loadGuildOptions();
        return true;
    }

    private void loadGuildOptions(){
        for(IGuild guild : bot.getClient().getGuilds()){
            GuildOptions guildOptions = bot.getGuildOptions(guild);
            audioManager.setVolume(guild, guildOptions.BOT_VOLUME);
        }
        //TODO: Load playlists into memory
    }

    private void registerYoutube(){
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) {
            }
        }).setApplicationName("youtube-cmdline-search-sample").build();
    }
    public SpecialBot getBot() {
        return bot;
    }
    public YouTube getYoutube(){
        return youtube;
    }
    public YoutubeWrapper getYoutubeWrapper(){
        return youtubeWrapper;
    }
    public Downloader getDownloader(){
        return downloader;
    }
    public AudioManager getAudioManager(){
        return audioManager;
    }

    public File getMusicDirectory(){
        return music_dir;
    }
    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
