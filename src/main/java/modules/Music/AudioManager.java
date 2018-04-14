package modules.Music;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.*;
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

public class AudioManager{

    private Music music;

    public AudioManager(Music music) {
        this.music = music;
    }

    //TODO Playlists, Download song only
    private ConcurrentHashMap<IGuild, IChannel> lastChannelControlledFrom = new ConcurrentHashMap<>();

	/*
	Audio player methods
	 */

    public void queueYoutube(IChannel channel, String url, String title) {
        music.getDownloader().getDownloadThreads().submit(() -> {
            if (music.getYoutubeWrapper().getVideoDuration(YoutubeWrapper.getIdFromUrl(url)) > 1000 * 60 * 10) {
                music.getBot().sendChannelMessage("That video is too long to play! Videos must be under 10 minutes", channel);
                return;
            }
            File audioFile = music.getDownloader().downloadYoutubeURL(url);
            if (audioFile == null) {
                music.getBot().sendChannelMessage("Audio file received as null from download. Contact an administrator", channel);
            } else if (!audioFile.exists())
                music.getBot().sendChannelMessage("That file doesn't exist! Contact an administrator", channel);
            else if (!audioFile.canRead())
                music.getBot().sendChannelMessage("I don't have access to that file! Contact an administrator", channel);
            else {
                try {
                    Track t = getAudioPlayer(channel.getGuild()).queue(audioFile);
                    setupTrack(t, audioFile, title);
                } catch (IOException e) {
                    music.getBot().sendChannelMessage("An IO exception occured: " + e.getMessage(), channel);
                } catch (UnsupportedAudioFileException e) {
                    music.getBot().sendChannelMessage("That type of file is not supported!", channel);
                }
            }
        });
    }

    /*
        End-User Methods
     */
    public void pauseTrack(IGuild guild, boolean pause) {
        getAudioPlayer(guild).setPaused(pause);
    }

    public void skipTrack(IGuild guild) {
        getAudioPlayer(guild).skip();
    }

    //TODO: Make volume persistent through shutdowns
    public void setVolume(IGuild guild, int percent){
        float volume = (float) (percent)/100;
        if(volume > 1.5) volume = 1.5f;
        if(volume < 0) volume = 0f;
        getAudioPlayer(guild).setVolume(volume);
    }
    public void shuffle(IGuild guild){
        getAudioPlayer(guild).shuffle();
    }
    public void setLooping(IGuild guild, boolean loop){
        getAudioPlayer(guild).setLoop(loop);
    }
    public boolean isLooping(IGuild guild){
        return getAudioPlayer(guild).isLooping();
    }

    /*
        Utility Methods
     */
    public AudioPlayer getAudioPlayer(IGuild guild) {
        return AudioPlayer.getAudioPlayerForGuild(guild);
    }

    public String getTrackTitle(Track track) {
        return track.getMetadata().containsKey("title") ? String.valueOf(track.getMetadata().get("title")) : "Unknown Track";
    }

    public Duration getTrackLength(Track track){
        return Duration.ofMillis(track.getTotalTrackTime());
    }

    public Duration getTrackPosition(Track track){
        return Duration.ofMillis(track.getCurrentTrackTime());
    }

    private void setupTrack(Track track, File file, String title){
        track.getMetadata().put("file", file);
        track.getMetadata().put("title", title);
    }

    public IChannel getLastChannelControlledFrom(IGuild guild){
        return lastChannelControlledFrom.get(guild);
    }
    public void setLastChannelControlledFrom(IGuild guild, IChannel channel){
        lastChannelControlledFrom.put(guild, channel);
    }




}
