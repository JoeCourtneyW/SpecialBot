package modules.Music;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import main.SpecialBot;
import modules.SpecialModule;

import java.io.IOException;

public class Music extends SpecialModule {
    private String name = "Music";
    private String version = "1.0";

    public static YouTube youtube;
    public AudioManager audioManager;

    public Music(SpecialBot bot){
        super(bot);
    }

    public boolean enable() {
        registerYoutube();
        audioManager = new AudioManager(bot);
        registerCommands(audioManager);
        registerHandlers(audioManager);
        return true;
    }
    private static void registerYoutube(){
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("youtube-cmdline-search-sample").build();
    }
    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
