package discord.modules.audio;

import java.io.IOException;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.modules.IModule;

public class Audio implements IModule{

	private String moduleName = "Audio";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.3.0";
	private String author = "SlyVitality";
	private Object handler = new AudioManager();
    public static YouTube youtube;
	public static IDiscordClient client;
	

    public boolean enable(IDiscordClient dclient) {
    	client = dclient;
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(handler);
		registerYoutube();
		System.out.println("[Module] Enabled " + moduleName + " V" + moduleVersion + " by " + author);
		return true;
    }
	public void disable() {

    }
	private static void registerYoutube(){
        youtube = new YouTube.Builder(new NetHttpTransport(), new JacksonFactory(), new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("youtube-cmdline-search-sample").build();
	}
    public String getAuthor() {
        return author;
    }
    public String getMinimumDiscord4JVersion() {
        return moduleMinimumVersion;
    }
    public String getName() {
        return moduleName;
    }
    public String getVersion() {
        return moduleVersion;
    }
}
