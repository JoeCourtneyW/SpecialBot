package modules.Music;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import main.Commands.Command;
import main.Commands.CommandExecutor;
import main.Main;
import main.SpecialBot;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.audio.AudioPlayer;
import sx.blah.discord.util.audio.AudioPlayer.Track;
import sx.blah.discord.util.audio.events.TrackQueueEvent;
import sx.blah.discord.util.audio.events.TrackStartEvent;
import utils.LoggerUtil;

@SuppressWarnings("unused")
public class AudioManager extends CommandExecutor {

    private static SpecialBot bot;
    private static File music_dir;

    public AudioManager(SpecialBot bot) {
        super(bot);
        AudioManager.bot = bot;
        music_dir = new File(Main.DIR + File.separator + "music" + File.separator);
    }

    //TODO Playlists, Download song only
    static ConcurrentHashMap<IGuild, IChannel> lastChannel = new ConcurrentHashMap<>();

    @Command(label = "queue", description = "Add a song to the song queue")
    public static void queueCommand(IMessage im) {
        lastChannel.put(im.getGuild(), im.getChannel());
        String[] split = im.getContent().split(" ");
        String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];
        if (args.length < 1) {
            StringBuilder sb = new StringBuilder();
            long total = 0;
            for (int i = 0; i < getPlayer(im.getGuild()).getPlaylistSize(); i++) {
                Track track = getPlayer(im.getGuild()).getPlaylist().get(i);
                File f = (File) track.getMetadata().get("file");
                long dur = 0;
                try {
                    dur = getDuration(f);
                } catch (Exception e) {
                    bot.sendChannelMessage("An internal error occured (IOE)", im.getChannel());
                }

                if (i == 0) {
                    sb.append("Playing: **").append(getTrackTitle(track)).append("** - *").append(getCurrentTrackTime(track)).append("*\n");
                    total += (dur - track.getCurrentTrackTime());
                } else {
                    sb.append((i)).append(") **").append(getTrackTitle(track)).append("** - *").append(getTrackLength(track)).append("*\n");
                    total += dur;
                }
            }
            sb.append("Total Queue Length: ***").append(convertMilli(total)).append("***");
            if (total > 0)
                bot.sendChannelMessage(sb.toString(), im.getChannel());
            else
                bot.sendChannelMessage("The queue is empty!", im.getChannel());
            return;

        }
        if (bot.getClient().getConnectedVoiceChannels().size() == 0) {
            bot.tryDiscordFunction(() ->
                    join(im.getChannel(), im.getAuthor()));
        }
        if (isURL(args[0])) {
            bot.tryDiscordFunction(() ->
                    queueYoutube(im.getChannel(), args[0], YoutubeWrapper.getTitle(args[0])));

        } else {
            StringBuilder sb = new StringBuilder();
            for (String a : args) {
                sb.append(a).append(" ");
            }
            sb.setLength(sb.length() - 1);
            String id;
            String title;
            try {
                String[] data = YoutubeWrapper.search(sb.toString());
                id = data[0];
                title = data[1];
            } catch (IOException e) {
                bot.sendChannelMessage("An internal error occured (IOE)", im.getChannel());
                return;
            }
            String url = "https://www.youtube.com/watch?v=" + id;
            bot.tryDiscordFunction(() ->
                    queueYoutube(im.getChannel(), url, title));
        }
        //MessageUtils.sendChannelMessage("That is not a valid Youtube URL!", im.getChannel());


    }

    private static boolean isURL(String url) {
        try {
            new URL(url);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Command(label = "unpause", description = "Unpause the music")
    public static void playCommand(IMessage im) {
        lastChannel.put(im.getGuild(), im.getChannel());
        if (bot.getClient().getConnectedVoiceChannels().size() == 0) {
            bot.tryDiscordFunction(() ->
                    join(im.getChannel(), im.getAuthor()));
        }
        pause(im.getChannel(), false);
    }

    @Command(label = "pause", description = "Pause the music")
    public static void pauseCommand(IMessage im) {
        lastChannel.put(im.getGuild(), im.getChannel());
        pause(im.getChannel(), true);
    }

    @Command(label = "skip", description = "Skip the current song")
    public static void skipCommand(IMessage im) {
        lastChannel.put(im.getGuild(), im.getChannel());
        skip(im.getChannel());
        pause(im.getChannel(), false);
    }

    @Command(label = "volume", description = "Change the volume of the client")
    public static void volumeCommand(IMessage im) {
        lastChannel.put(im.getGuild(), im.getChannel());
        try {
            volume(im.getChannel(), Integer.parseInt(im.getContent().split(" ")[1]));
        } catch (NumberFormatException e) {
            bot.sendChannelMessage("Invalid Volume Percentage", im.getChannel());
        }
    }

	/*
	Track events
	 */

    @EventSubscriber
    public void onTrackQueue(final TrackQueueEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
        final IGuild guild = event.getPlayer().getGuild();
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                bot.tryDiscordFunction(() ->
                        lastChannel.get(guild).sendMessage("Added **" + getTrackTitle(event.getTrack()) + "** to the queue."));
            }
        }, 500);
    }

    @EventSubscriber
    public void onTrackStart(TrackStartEvent event) {
        IGuild guild = event.getPlayer().getGuild();
        bot.sendChannelMessage("Now playing **" + getTrackTitle(event.getTrack()) + "**.", lastChannel.get(guild));
    }

	/*
	Audio player methods
	 */

    public static void join(IChannel channel, IUser user) throws RateLimitException, DiscordException, MissingPermissionsException {
        if (user.getVoiceStates().size() < 1)
            channel.sendMessage("You aren't in a voice channel!");
        else {
            IVoiceChannel voice = user.getVoiceStateForGuild(channel.getGuild()).getChannel();
            if (!voice.getModifiedPermissions(bot.getClient().getOurUser()).contains(Permissions.VOICE_CONNECT))
                channel.sendMessage("I can't join that voice channel!");
            else if (voice.getConnectedUsers().size() >= voice.getUserLimit() && voice.getUserLimit() > 0)
                channel.sendMessage("That room is full!");
            else {
                voice.join();
            }
        }
    }

    public static void queueYoutube(IChannel channel, String url, String title) {
        bot.getAsyncExecutor().submit(() -> {
            if (YoutubeWrapper.getDuration(getYoutubeIdFromUrl(url)) > 1000 * 60 * 10) {
                bot.sendChannelMessage("That video is too long to play! Videos must be under 10 minutes", channel);
                return;
            }
            File audioFile = downloadYoutubeURL(url, title, channel.getGuild());
            if (audioFile == null) {
                bot.sendChannelMessage("Audio file received as null from download", channel);
            } else if (!audioFile.exists())
                bot.sendChannelMessage("That file doesn't exist!", channel);
            else if (!audioFile.canRead())
                bot.sendChannelMessage("I don't have access to that file!", channel);
            else {
                try {
                    LoggerUtil.DEBUG("Adding file to queue");
                    Track t = getPlayer(channel.getGuild()).queue(audioFile);
                    setTrackTitle(t, title);
                    setTrackFile(t, audioFile);
                } catch (IOException e) {
                    bot.sendChannelMessage("An IO exception occured: " + e.getMessage(), channel);
                } catch (UnsupportedAudioFileException e) {
                    bot.sendChannelMessage("That type of file is not supported!", channel);
                }
            }
        });
    }

    public static void pause(IChannel channel, boolean pause) {
        getPlayer(channel.getGuild()).setPaused(pause);
    }

    public static void skip(IChannel channel) {
        getPlayer(channel.getGuild()).skip();
    }

    public static void volume(IChannel channel, int percent) {
        volume(channel, (float) (percent) / 100);
    }

    private static void volume(IChannel channel, Float vol) {
        if (vol > 1.5) vol = 1.5f;
        if (vol < 0) vol = 0f;
        getPlayer(channel.getGuild()).setVolume(vol);
        bot.sendChannelMessage("Set volume to **" + (int) (vol * 100) + "%**.", channel);
    }

    /*
    Utility methods
     */
    private static AudioPlayer getPlayer(IGuild guild) {
        return AudioPlayer.getAudioPlayerForGuild(guild);
    }

    private static String getTrackTitle(Track track) {
        return track.getMetadata().containsKey("title") ? String.valueOf(track.getMetadata().get("title")) : "Unknown Track";
    }

    private static String getTrackLength(Track track) {
        File file = (File) track.getMetadata().get("file");
        long dur = getDuration(file);
        return String.format("%dm %02ds",
                TimeUnit.MILLISECONDS.toMinutes(dur),
                TimeUnit.MILLISECONDS.toSeconds(dur) % 60
        );
    }

    private static String getCurrentTrackTime(Track track) {
        File file = (File) track.getMetadata().get("file");
        long dur = getDuration(file);
        return String.format("%dm %02ds / %dm %02ds",
                TimeUnit.MILLISECONDS.toMinutes(track.getCurrentTrackTime()),
                TimeUnit.MILLISECONDS.toSeconds(track.getCurrentTrackTime()) % 60,
                TimeUnit.MILLISECONDS.toMinutes(dur),
                TimeUnit.MILLISECONDS.toSeconds(dur) % 60
        );
    }

    private static String convertMilli(long milli) {
        return String.format("%dm %02ds",
                TimeUnit.MILLISECONDS.toMinutes(milli),
                TimeUnit.MILLISECONDS.toSeconds(milli) % 60
        );
    }

    private static void setTrackTitle(Track track, String title) {
        track.getMetadata().put("title", title);
    }

    private static void setTrackFile(Track track, File f) {
        track.getMetadata().put("file", f);
    }

    private static long getDuration(File file) {
        AudioFileFormat fileFormat;
        try {
            fileFormat = AudioSystem.getAudioFileFormat(file);
        } catch (IOException | UnsupportedAudioFileException e) {
            return 0;
        }
        if (fileFormat instanceof TAudioFileFormat) {
            Map<?, ?> properties = fileFormat.properties();
            String key = "duration";
            Long microseconds = (Long) properties.get(key);
            return microseconds / 1000;
        } else {
            return 0;
        }

    }

    public static String getYoutubeIdFromUrl(String url) {
        String id;
        if (url.contains("youtu.be")) {
            id = url.split("/")[1];
        } else {
            id = url.split("\\?v=")[1];
            if (id.contains("&"))
                id = id.split("&")[0];
        }
        return id;
    }

    private static boolean downloading = false;

    private static File downloadYoutubeURL(final String url, String title, IGuild g) {
        final String id = getYoutubeIdFromUrl(url);
        String path = music_dir.getPath() + File.separator;

        path += id + ".mp3";
        if (new File(path).exists()) {
            LoggerUtil.DEBUG("File already downloaded, continuing");
            return new File(path);
        }
        if (downloading) {
            bot.sendChannelMessage("I'm already downloading a file! Please wait", lastChannel.get(g));
            return null;
        }

        bot.sendChannelMessage("Downloading file...", lastChannel.get(g));
        try {
            downloading = true;
            Process p = Runtime.getRuntime().exec("sudo youtube-dl --id --extract-audio --audio-format mp3 " + url, null, music_dir);
            LoggerUtil.DEBUG("Downloading youtube mp3 from " + url);
            p.waitFor();
            LoggerUtil.DEBUG("File downloaded");
            downloading = false;
        } catch (IOException | InterruptedException e) {
            downloading = false;
        }

        return new File(path);
    }
}
