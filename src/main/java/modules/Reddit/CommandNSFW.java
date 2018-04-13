package modules.Reddit;

import main.Commands.Command;
import main.Commands.CommandExecutor;
import main.Main;
import main.SpecialBot;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;
import sx.blah.discord.handle.obj.IMessage;
import utils.LoggerUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CommandNSFW extends CommandExecutor {

    private static HashMap<String, ArrayList<Submission>> cache = new HashMap<>();
    private static String defaultSubreddit = "nsfw";
    private static String defaultMultireddit = "nsfw";
    private static SpecialBot bot;

    public CommandNSFW(SpecialBot bot) {
        super(bot);
        CommandNSFW.bot = bot;
    }

    @Command(label = "nsfw")
    public static void onNSFW(IMessage message) {
        String[] args = message.getContent().split(" ");
        ArrayList<Submission> images = new ArrayList<>();
        boolean multiReddit = false;
        String search = defaultSubreddit;
        int limit = 100;

        if (args.length >= 3) {
            multiReddit = args[1].toLowerCase().contains("m");
            search = args[2];
        }
        ArrayList<Submission> listing;
        if (cache.get(search) == null) {
            DefaultPaginator<Submission> aggregator;
            DefaultPaginator.Builder<Submission, SubredditSort> builder;
            if (multiReddit)
                builder = Reddit.reddit.me().multi(search).posts();
            else
                builder = Reddit.reddit.subreddit(search).posts();
            aggregator = builder
                    .limit(limit)
                    .sorting(SubredditSort.TOP)
                    .timePeriod(TimePeriod.YEAR)
                    .build();
            for (Submission s : aggregator.next()) {
                if (!s.isSelfPost()) {
                    images.add(s);
                }
            }
            cache.put(search, images);
            listing = images;
        } else {
            listing = cache.get(search);
        }
        int index = new Random().nextInt(listing.size());
        Submission post = listing.get(index);
        while (true) {
            if (post.getDomain().contains("imgur.com") || post.getDomain().contains("i.redd.it") || post.getDomain().contains("gfycat.com")) {
                bot.sendChannelMessage("[r/" + post.getSubreddit() + "] " + "*" + post.getTitle() + "*" + "\n" + post.getUrl(), message.getChannel());
                return;
            } else {
                index = new Random().nextInt(listing.size());
                post = listing.get(index);
            }
        }
    }
}
