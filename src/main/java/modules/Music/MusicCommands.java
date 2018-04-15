package modules.Music;

import javafx.util.Pair;
import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.JsonObjects.GuildOptions;
import main.SpecialBot;
import sx.blah.discord.util.audio.AudioPlayer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.StringJoiner;

public class MusicCommands extends CommandExecutor {
    private Music music;
    private AudioManager audioManager;

    public MusicCommands(SpecialBot bot) {
        super(bot);
        this.music = (Music) bot.getModule("Music");
        this.audioManager = music.getAudioManager();
    }

    @Command(label = "queue", description = "Add a song to the song queue", alias = "play")
    public void queueCommand(CommandEvent event) {
        music.getAudioManager().setLastChannelControlledFrom(event.getGuild(), event.getChannel());
        if (event.getArgs().length < 1) { //Show current queue if they aren't trying to queue a new song
            StringBuilder queueList = new StringBuilder();
            long total = 0;
            for (int i = 0; i < audioManager.getAudioPlayer(event.getChannel().getGuild()).getPlaylistSize(); i++) {

                AudioPlayer.Track track = audioManager.getAudioPlayer(event.getChannel().getGuild()).getPlaylist().get(i);
                long trackDuration = audioManager.getTrackLength(track).toMillis();

                if (i == 0) {
                    queueList.append("Playing: **").append(audioManager.getTrackTitle(track)).append("** - *")
                            .append(getReadableDuration(audioManager.getTrackPosition(track))).append("*")
                            .append(" */* ")
                            .append(getReadableDuration(audioManager.getTrackLength(track))).append("*\n");
                    total += (trackDuration - audioManager.getTrackPosition(track).toMillis());
                } else {
                    queueList.append((i)).append(") **").append(audioManager.getTrackTitle(track)).append("** - *")
                            .append(getReadableDuration(audioManager.getTrackLength(track))).append("*\n");
                    total += trackDuration;
                }
            }
            queueList.append("Total Queue Length: ***").append(getReadableDuration(Duration.ofMillis(total))).append("***");
            if (total > 0)
                bot.sendChannelMessage(queueList.toString(), event.getChannel());
            else
                bot.sendChannelMessage("The queue is empty!", event.getChannel());
            return;

        }
        if (bot.getClient().getConnectedVoiceChannels().size() == 0) { //Make sure to join the voice channel before trying to play
            if (event.getAuthor().getVoiceStateForGuild(event.getChannel().getGuild()) != null) //If the user is in a voice channel
                bot.joinVoiceChannel(event.getAuthor().getVoiceStateForGuild(event.getChannel().getGuild()).getChannel());
            else
                bot.joinVoiceChannel(event.getChannel().getGuild().getVoiceChannels().get(0)); //If the user isn't connected to a voice channel, join the (presumably) lobby
        }
        if (isURL(event.getArgs()[0]) != null) { //The user provided a direct youtube link. We can grab the id from that: no need to search
            audioManager.queueYoutube(event.getChannel(), event.getArgs()[0], music.getYoutubeWrapper().getVideoTitle(YoutubeWrapper.getIdFromUrl(event.getArgs()[0])));
        } else { //Search the youtube library and try to find the song provided
            StringJoiner query = new StringJoiner(" ");
            for (String word : event.getArgs()) {
                query.add(word);
            }
            Pair<String, String> video;
            try {
                video = music.getYoutubeWrapper().searchForVideo(query.toString());
            } catch (IOException e) {
                bot.sendChannelMessage("An internal error occured while trying to search YouTube with the given query. Contact an administrator", event.getChannel());
                return;
            }
            String url = "https://www.youtube.com/watch?v=" + video.getKey();
            audioManager.queueYoutube(event.getChannel(), url, video.getValue());
        }


    }

    @Command(label = "bring", description = "Brings the bot to the user's current voice channel")
    public void bring(CommandEvent event) {
        audioManager.setLastChannelControlledFrom(event.getChannel().getGuild(), event.getChannel());
        if (event.getAuthor().getVoiceStateForGuild(event.getChannel().getGuild()) != null) //If the user is in a voice channel
            bot.joinVoiceChannel(event.getAuthor().getVoiceStateForGuild(event.getChannel().getGuild()).getChannel());
        else
            bot.sendChannelMessage("You are not current in a voice channel", event.getChannel());
    }

    @Command(label = "rewind", description = "Rewinds the current song to the beginning")
    public void rewind(CommandEvent event) {
        audioManager.setLastChannelControlledFrom(event.getChannel().getGuild(), event.getChannel());
        audioManager.setPosition(event.getGuild(), 0);
    }

    @Command(label = "unpause", description = "Unpause the music")
    public void unpause(CommandEvent event) {
        audioManager.setLastChannelControlledFrom(event.getChannel().getGuild(), event.getChannel());
        audioManager.pauseTrack(event.getChannel().getGuild(), false);
    }

    @Command(label = "pause", description = "Pause the music")
    public void pauseCommand(CommandEvent event) {
        audioManager.setLastChannelControlledFrom(event.getChannel().getGuild(), event.getChannel());
        audioManager.pauseTrack(event.getChannel().getGuild(), true);
    }

    @Command(label = "skip", description = "Skip the current song")
    public void skipCommand(CommandEvent event) {
        audioManager.setLastChannelControlledFrom(event.getChannel().getGuild(), event.getChannel());
        audioManager.skipTrack(event.getChannel().getGuild());
        audioManager.pauseTrack(event.getChannel().getGuild(), false);
    }

    @Command(label = "volume", description = "Change the volume of the client")
    public void volumeCommand(CommandEvent event) {
        if (event.getArgs().length == 0) {
            bot.sendChannelMessage("Current Volume: " + audioManager.getVolume(event.getGuild()) + "%", event.getChannel());
            return;
        }
        audioManager.setLastChannelControlledFrom(event.getChannel().getGuild(), event.getChannel());
        try {
            audioManager.setVolume(event.getChannel().getGuild(), Integer.parseInt(event.getArgs()[0]));
        } catch (NumberFormatException e) {
            bot.sendChannelMessage("Invalid Volume Percentage, Ex: 100", event.getChannel());
            return;
        }
        GuildOptions options = bot.getGuildOptions(event.getChannel().getGuild());
        options.BOT_VOLUME = audioManager.getVolume(event.getGuild());
        bot.updateGuildOptions(options);
    }

    @Command(label = "loop", description = "Toggle the loop state of the client")
    public void loopCommand(CommandEvent event) {
        audioManager.setLastChannelControlledFrom(event.getChannel().getGuild(), event.getChannel());
        audioManager.setLooping(event.getChannel().getGuild(), !audioManager.isLooping(event.getChannel().getGuild()));
    }

    @Command(label = "shuffle", description = "Shuffle the current queue")
    public void shuffleCommand(CommandEvent event) {
        audioManager.setLastChannelControlledFrom(event.getChannel().getGuild(), event.getChannel());

        audioManager.shuffle(event.getChannel().getGuild());
    }

    //TODO: Make this the youtube regex checker as well
    private URL isURL(String url) {
        try {
            new URL(url);
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private String getReadableDuration(Duration duration) {
        StringBuilder format = new StringBuilder();
        if (duration.toMinutes() >= 60) {
            format.append(((int) Math.floor(duration.toHours())));
            format.append("h ");
        }
        if (duration.toMinutes() % 60 > 0) {
            format.append(((int) Math.floor(duration.toMinutes())) % 60);
            format.append("m ");
        }
        if (duration.getSeconds() % 3600 > 0) {
            format.append(((int) Math.floor(duration.getSeconds())) % 3600);
            format.append("s");
        }
        return format.toString();
    }
}
