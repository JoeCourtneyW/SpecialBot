package modules.Reddit;

import main.Main;
import main.SpecialBot;
import modules.SpecialModule;
import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

import java.io.File;

public class Reddit extends SpecialModule{

    private String name = "Reddit";
    private String version = "1.0";

    public static RedditClient reddit; //TODO: Passthrough?

    public Reddit(SpecialBot bot){
        super(bot);
    }

    public boolean enable() {
        if (!new File(Main.DIR + "/nsfw").exists()) {
            new File(Main.DIR + "/nsfw").mkdir();
        }
        authenticate();
        registerCommands(new CommandNSFW(bot));
        return true;
    }

    private void authenticate(){
        Credentials credentials = Credentials.script(
                Main.CREDENTIALS.get("REDDIT_USER").asText(),
                Main.CREDENTIALS.get("REDDIT_PASSWORD").asText(),
                Main.CREDENTIALS.get("REDDIT_CLIENT_ID").asText(),
                Main.CREDENTIALS.get("REDDIT_SECRET_KEY").asText());

        // Construct our NetworkAdapter
        UserAgent userAgent = new UserAgent(
                "bot",
                "modules.Reddit.Reddit",
                Version.get(),
                Main.CREDENTIALS.get("REDDIT_USER").asText());

        NetworkAdapter http = new OkHttpNetworkAdapter(userAgent);

        // Authenticate our client
        reddit = OAuthHelper.automatic(http, credentials);
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
