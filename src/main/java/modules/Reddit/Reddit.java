package modules.Reddit;

import main.Main;
import main.SpecialModule;
import net.dean.jraw.RedditClient;
import net.dean.jraw.Version;
import net.dean.jraw.http.NetworkAdapter;
import net.dean.jraw.http.OkHttpNetworkAdapter;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.oauth.Credentials;
import net.dean.jraw.oauth.OAuthHelper;

public class Reddit implements SpecialModule {

    static RedditClient reddit; //TODO: Passthrough?


    public boolean onLoad() {
        authenticate();
        bot.registerCommands(new CommandNSFW());
        return true;
    }

    private void authenticate() {
        Credentials credentials = Credentials.script(
                Main.CREDENTIALS.REDDIT_USER,
                Main.CREDENTIALS.REDDIT_PASSWORD,
                Main.CREDENTIALS.REDDIT_CLIENT_ID,
                Main.CREDENTIALS.REDDIT_SECRET_KEY);

        // Construct our NetworkAdapter
        UserAgent userAgent = new UserAgent(
                "bot",
                "modules.Reddit.Reddit",
                Version.get(),
                Main.CREDENTIALS.REDDIT_USER);

        NetworkAdapter http = new OkHttpNetworkAdapter(userAgent);

        // Authenticate our client
        reddit = OAuthHelper.automatic(http, credentials);
    }

    public String getName() {
        return "Reddit";
    }

    public String getVersion() {
        return "1.1";
    }
}
