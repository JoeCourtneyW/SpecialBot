package modules.Reddit;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.SpecialBot;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;

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
    public static void onNSFW(CommandEvent event) {
        ArrayList<Submission> images = new ArrayList<>();
        boolean multiReddit = false;
        String search = defaultSubreddit;
        int limit = 100;

        if (event.getArgs().length == 2) {
            multiReddit = event.getArgs()[0].toLowerCase().contains("m");
            search = event.getArgs()[1];
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
                bot.sendChannelMessage("[r/" + post.getSubreddit() + "] " + "*" + post.getTitle() + "*" + "\n" + post.getUrl(), event.getChannel());
                return;
            } else {
                index = new Random().nextInt(listing.size());
                post = listing.get(index);
            }
        }
    }
}
