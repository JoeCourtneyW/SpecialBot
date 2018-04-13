package modules.Music;

import javafx.util.Pair;
import main.Commands.Command;
import main.Commands.CommandExecutor;
import main.SpecialBot;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.util.audio.AudioPlayer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    public void queueCommand(IMessage im) {
        music.getAudioManager().setLastChannelControlledFrom(im.getGuild(), im.getChannel());
        String[] split = im.getContent().split(" ");
        String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];
        if (args.length < 1) { //Show current queue if they aren't trying to queue a new song
            StringBuilder queueList = new StringBuilder();
            long total = 0;
            for (int i = 0; i < audioManager.getAudioPlayer(im.getGuild()).getPlaylistSize(); i++) {

                AudioPlayer.Track track = audioManager.getAudioPlayer(im.getGuild()).getPlaylist().get(i);
                long trackDuration = audioManager.getTrackLength(track).toMillis();

                if (i == 0) {
                    queueList.append("Playing: **").append(audioManager.getTrackTitle(track)).append("** - *")
                            .append(audioManager.getTrackPosition(track)).append("*\n");
                    total += (trackDuration - track.getCurrentTrackTime());
                } else {
                    queueList.append((i)).append(") **").append(audioManager.getTrackTitle(track)).append("** - *")
                            .append(audioManager.getTrackLength(track)).append("*\n");
                    total += trackDuration;
                }
            }
            queueList.append("Total Queue Length: ***").append(Duration.ofMillis(total)).append("***");
            if (total > 0)
                bot.sendChannelMessage(queueList.toString(), im.getChannel());
            else
                bot.sendChannelMessage("The queue is empty!", im.getChannel());
            return;

        }
        if (bot.getClient().getConnectedVoiceChannels().size() == 0) { //Make sure to join the voice channel before trying to play
            if (im.getAuthor().getVoiceStateForGuild(im.getGuild()) != null) //If the user is in a voice channel
                bot.joinVoiceChannel(im.getAuthor().getVoiceStateForGuild(im.getGuild()).getChannel());
            else
                bot.joinVoiceChannel(im.getGuild().getVoiceChannels().get(0)); //If the user isn't connected to a voice channel, join the (presumably) lobby
        }
        if (isURL(args[0]) != null) { //The user provided a direct youtube link. We can grab the id from that: no need to search
            audioManager.queueYoutube(im.getChannel(), args[0], music.getYoutubeWrapper().getVideoTitle(YoutubeWrapper.getIdFromUrl(args[0])));
        } else { //Search the youtube library and try to find the song provided
            StringJoiner query = new StringJoiner(" ");
            for (String word : args) {
                query.add(word);
            }
            Pair<String, String> video;
            try {
                video = music.getYoutubeWrapper().searchForVideo(query.toString());
            } catch (IOException e) {
                bot.sendChannelMessage("An internal error occured while trying to search YouTube with the given query. Contact an administrator", im.getChannel());
                return;
            }
            String url = "https://www.youtube.com/watch?v=" + video.getKey();
            audioManager.queueYoutube(im.getChannel(), url, video.getValue());
        }


    }

    @Command(label = "unpause", description = "Unpause the music")
    public void playCommand(IMessage im) {
        audioManager.setLastChannelControlledFrom(im.getGuild(), im.getChannel());
        if (bot.getClient().getConnectedVoiceChannels().size() == 0) {
            bot.joinVoiceChannel(im.getChannel());
            audioManager.pauseTrack(im.getGuild(), false);
        }
    }

    @Command(label = "pause", description = "Pause the music")
    public void pauseCommand(IMessage im) {
        audioManager.setLastChannelControlledFrom(im.getGuild(), im.getChannel());
        audioManager.pauseTrack(im.getGuild(), true);
    }

    @Command(label = "skip", description = "Skip the current song")
    public void skipCommand(IMessage im) {
        audioManager.setLastChannelControlledFrom(im.getGuild(), im.getChannel());
        audioManager.skipTrack(im.getGuild());
        audioManager.pauseTrack(im.getGuild(), false);
    }

    @Command(label = "volume", description = "Change the volume of the client")
    public void volumeCommand(IMessage im) {
        audioManager.setLastChannelControlledFrom(im.getGuild(), im.getChannel());
        try {
            audioManager.setVolume(im.getGuild(), Integer.parseInt(im.getContent().split(" ")[1]));
        } catch (NumberFormatException e) {
            bot.sendChannelMessage("Invalid Volume Percentage, Ex: 100", im.getChannel());
        }
    }

    @Command(label = "loop", description = "Toggle the loop state of the client")
    public void loopCommand(IMessage im) {
        audioManager.setLastChannelControlledFrom(im.getGuild(), im.getChannel());
        audioManager.setLooping(im.getGuild(), !audioManager.isLooping(im.getGuild()));
    }

    @Command(label = "shuffle", description = "Shuffle the current queue")
    public void shuffleCommand(IMessage im) {
        audioManager.setLastChannelControlledFrom(im.getGuild(), im.getChannel());

        audioManager.shuffle(im.getGuild());
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
}
