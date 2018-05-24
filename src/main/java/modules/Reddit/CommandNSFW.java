package modules.Reddit;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.SubredditSort;
import net.dean.jraw.models.TimePeriod;
import net.dean.jraw.pagination.DefaultPaginator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CommandNSFW implements CommandExecutor {

    private static HashMap<String, ArrayList<Submission>> cache = new HashMap<>();
    private static String defaultSubreddit = "nsfw";
    private static String defaultMultireddit = "nsfw";

    @Command(label = "nsfw")
    public static void onNSFW(CommandEvent event) {
        ArrayList<Submission> images = new ArrayList<>();
        boolean multiReddit;
        String search;
        int limit = 100;

        if (event.getArgs().length == 2) {
            multiReddit = event.getArgs()[0].toLowerCase().contains("m");
            search = event.getArgs()[1].toLowerCase();
        } else if (event.getArgs().length == 0) {
            multiReddit = false;
            search = defaultSubreddit;
        } else {
            event.reply("Incorrect usage: .nsfw [m, s] [sub/multireddit name]");
            return;
        }

        if (multiReddit) {
            try {
                Reddit.reddit.me().multi(search).about();
            } catch (NullPointerException e) {
                event.reply("That multreddit does not exist!");
                return;
            }
        } else {
            try {
                Reddit.reddit.subreddit(search).about();
            } catch (NullPointerException e) {
                event.reply("That subreddit does not exist!");
                return;
            }
        }

        ArrayList<Submission> listing;
        if (!cache.containsKey(search)) {
            DefaultPaginator<Submission> aggregator;
            DefaultPaginator.Builder<Submission, SubredditSort> builder;
            if (multiReddit)
                builder = Reddit.reddit.me().multi(search).posts();
            else
                builder = Reddit.reddit.subreddit(search).posts();
            aggregator = builder
                    .limit(limit)
                    .sorting(SubredditSort.HOT)
                    .timePeriod(TimePeriod.DAY)
                    .build();
            //If the post is not a self post, cache it
            aggregator.next().stream()
                    .filter(post -> !post.isSelfPost())
                    .filter(post -> post.getDomain().contains("imgur.com")
                            || post.getDomain().contains("i.redd.it")
                            || post.getDomain().contains("i.redditmedia.com")
                            || post.getDomain().contains("gyfcat.com"))
                    .forEach(images::add);
            cache.put(search, images);
            listing = images;
        } else {
            listing = cache.get(search);
        }
        Submission post = listing.get(new Random().nextInt(listing.size()));
        event.reply("[r/" + post.getSubreddit() + "] " + "*" + post.getTitle() + "*" + "\n" + post.getUrl());
    }
}
