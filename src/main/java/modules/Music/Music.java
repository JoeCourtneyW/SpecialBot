package modules.Music;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import main.Main;
import main.SpecialBot;
import modules.SpecialModule;

import java.io.File;

public class Music extends SpecialModule {
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
    }

    public boolean enable() {
        registerYoutube();
        registerCommands(new MusicCommands(bot));
        registerHandlers(new MusicHandler(bot));
        youtubeWrapper = new YoutubeWrapper(this);
        audioManager = new AudioManager(this);
        downloader = new Downloader(this);
        return true;
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
