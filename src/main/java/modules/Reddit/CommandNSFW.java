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


    //Stored with key: 'subredditname-timeperiod EX: nsfw-ALL
    //            value: List of 100 image posts in given subreddit sorted by TOP in the given time period
    private static HashMap<String, ArrayList<Submission>> cache = new HashMap<>();
    private static final String defaultSubreddit = "nsfw";

    @Command(label = "nsfw")
    public void onNSFW(CommandEvent event) {
        ArrayList<Submission> images = new ArrayList<>();
        String search;
        TimePeriod period = TimePeriod.DAY;
        int limit = 100;

        if (event.getArgs().length == 2) {
            search = event.getArgs()[0].toLowerCase();
            try {
                period = TimePeriod.valueOf(event.getArgs()[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                event.reply("*Invalid time period, options: <ALL, YEAR, MONTH, WEEK, DAY>*");
                return;
            }
        } else if (event.getArgs().length == 1) {
            search = event.getArgs()[0].toLowerCase();
        } else if (event.getArgs().length == 0) {
            search = defaultSubreddit;
        } else {
            event.reply("*Incorrect usage: .nsfw [subreddit name] <ALL, YEAR, MONTH, WEEK, DAY>*");
            return;
        }

        try {
            Reddit.reddit.subreddit(search).about();
        } catch (NullPointerException e) {
            event.reply("*That subreddit does not exist!*");
            return;
        }


        ArrayList<Submission> listing;
        if (!cache.containsKey(search + "-" + period.name())) {
            DefaultPaginator<Submission> aggregator;
            DefaultPaginator.Builder<Submission, SubredditSort> builder;
            builder = Reddit.reddit.subreddit(search).posts();
            aggregator = builder
                    .limit(limit)
                    .sorting(SubredditSort.TOP)
                    .timePeriod(period)
                    .build();
            //If the post is not a self post, cache it
            aggregator.next().stream()
                    .filter(post -> !post.isSelfPost())
                    .filter(post -> post.getDomain().contains("imgur.com")
                            || post.getDomain().contains("i.redd.it")
                            || post.getDomain().contains("i.redditmedia.com")
                            || post.getDomain().contains("gyfcat.com"))
                    .forEach(images::add);
            cache.put(search + "-" + period.name(), images);
            listing = images;
        } else {
            listing = cache.get(search + "-" + period.name());
        }
        Submission post = listing.get(new Random().nextInt(listing.size()));
        event.reply("[r/" + post.getSubreddit() + "] " + "*" + post.getTitle() + "*" + "\n" + post.getUrl());
    }
}
