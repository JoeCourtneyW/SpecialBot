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
        boolean download_all = false;
        int limit = 100;

        if(args.length == 4)
            if(args[3].equalsIgnoreCase("download"))
                download_all = true;

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
        if(download_all){
            //DUMB TEST TO DOWNLOAD ALL IMAGES, HAHAHA
            LoggerUtil.INFO("Downloading all images... This may take a second...");
            for(Submission post : listing){
                LoggerUtil.DEBUG(post.getUrl());
                if (post.getDomain().contains("i.imgur.com") || post.getDomain().contains("i.redd.it") || post.getDomain().contains("gfycat.com")) {
                    downloadImage(post);
                } else if (post.getDomain().contains("imgur.com")) {
                    String url = "https://i.imgur.com";
                    url += post.getUrl().substring(post.getUrl().lastIndexOf('/'));
                    url += ".jpg";
                    downloadImage(post.getSubreddit(), url, post.getId());
                }
            }
            return;
        }

        int index = new Random().nextInt(listing.size());
        Submission post = listing.get(index);
        while (true) {
            LoggerUtil.DEBUG(post.getUrl());
            String EXISTS_PATH = Main.DIR + "\\nsfw\\r\\" + post.getSubreddit() + "\\" + post.getId();
            if (post.getDomain().contains("i.imgur.com") || post.getDomain().contains("i.redd.it") || post.getDomain().contains("gfycat.com")) {
                File image = downloadImage(post);
                if (image.length() > 5000000 || image.getName().endsWith("mp4")) { //If the file size exceeds the 5MB maximum file size upload, just send the link. or if its an mp4
                    bot.sendChannelMessage("[r/" + post.getSubreddit() + "] " + "*" + post.getTitle() + "*" + "\n" + post.getUrl(), message.getChannel());
                    return;
                }
                bot.sendFile("[r/" + post.getSubreddit() + "] " + "*" + post.getTitle() + "*", image, message.getChannel());
                return;
            } else if (post.getDomain().contains("imgur.com")) {
                String url = "https://i.imgur.com";
                url += post.getUrl().substring(post.getUrl().lastIndexOf('/'));
                url += ".jpg";

                File image = downloadImage(post.getSubreddit(), url, post.getId());
                bot.sendFile("[r/" + post.getSubreddit() + "] " + "*" + post.getTitle() + "*", image, message.getChannel());
                return;
            } else {
                index = new Random().nextInt(listing.size());
                post = listing.get(index);
            }
        }
    }

    private static File downloadImage(Submission post) {
        return downloadImage(post.getSubreddit(), post.getUrl(), post.getId());
    }

    private static File downloadImage(String subreddit, String url, String id) {
        if (url.contains("?")) {
            url = url.split("\\?")[0];
        }
        String IMGUR_DL_LINK = "https://imgur.com/download";
        String GFYCAT_DL_LINK = "https://giant.gfycat.com";

        subreddit = "r" + "\\" + subreddit;
        String PATH = Main.DIR + "\\nsfw\\" + subreddit + "\\";
        if (!new File(PATH).exists()) new File(PATH).mkdir();
        PATH += id.trim();
        String EXTENSION = url.substring(url.lastIndexOf('.')).trim();

        if (url.contains("gfycat")) {
            url = GFYCAT_DL_LINK + url.substring(url.lastIndexOf('/')) + ".mp4";
            EXTENSION = ".mp4";
        }
        if (EXTENSION.equalsIgnoreCase(".gifv")) {
            url = IMGUR_DL_LINK + url.substring(url.lastIndexOf('/')).split("\\.")[0];
            EXTENSION = ".gif";
        }

        if (!new File(PATH + EXTENSION).exists()) {
            try (InputStream in = new URL(url).openStream()) {
                Files.copy(in, Paths.get(PATH + EXTENSION));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new File(PATH + EXTENSION);
    }

    public String getExtension(String url) {
        if (url.contains("?")) {
            url = url.split("\\?")[0];
        }
        String EXTENSION = url.substring(url.lastIndexOf('.')).trim();
        return ""; //TODO
    }
}
