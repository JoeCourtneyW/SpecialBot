package modules.Reddit;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.Commands.PermissionLevel;
import net.dean.jraw.models.Submission;
import net.dean.jraw.models.TimePeriod;

import java.util.HashMap;

public class CommandNSFW implements CommandExecutor {


    //Stored with key: 'subredditname-timeperiod EX: nsfw-ALL
    //            value: List of 100 image posts in given subreddit sorted by TOP in the given time period
    private static HashMap<String, SubredditCache> redditCaches = new HashMap<>();
    private static final String defaultSubreddit = "nsfw";

    @Command(label = "nsfw",
            description = "Pulls random images from nsfw subreddits",
            alias = "reddit",
            permissionLevel = PermissionLevel.PRIVILIGED_MEMBER)
    public void onNSFW(CommandEvent event) {
        if (!event.getChannel().isNSFW()) {
            event.reply("*This command can only be executed in an NSFW channel*");
        }
        String search;
        TimePeriod period = TimePeriod.DAY;

        if (event.getArgs().length == 2) { //2 Arguments: Subreddit, and time period
            search = event.getArgs()[0].toLowerCase();
            try {
                period = TimePeriod.valueOf(event.getArgs()[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                event.reply("*Invalid time period, options: <ALL, YEAR, MONTH, WEEK, DAY>*");
                return;
            }
        } else if (event.getArgs().length == 1) { //1 Argument: Just a subreddit
            search = event.getArgs()[0].toLowerCase();
        } else if (event.getArgs().length == 0) { //0 Argument: Return default subreddit
            search = defaultSubreddit;
        } else { //Show help message if more than 2
            event.reply("*Incorrect usage: .nsfw [subreddit name] <ALL, YEAR, MONTH, WEEK, DAY>*");
            return;
        }

        String cacheKey = search + "-" + period.name(); //Used to lookup in cache

        SubredditCache subredditCache;

        if (!redditCaches.containsKey(cacheKey)) {

            try { //Make sure the subreddit exists if we don't already have it cached
                Reddit.reddit.subreddit(search).about();
            } catch (NullPointerException e) {
                event.reply("*That subreddit does not exist!*");
                return;
            }

            subredditCache = new SubredditCache(search, period);
            redditCaches.put(cacheKey, subredditCache);
        } else {
            subredditCache = redditCaches.get(cacheKey);
        }


        Submission randomPost = subredditCache.getRandomSubmission(); //Grabs a random post and deletes it from the list

        event.reply("[r/" + randomPost.getSubreddit() + "] " + "*" + randomPost.getTitle() + "*" + "\n" + randomPost.getUrl());
    }
}
