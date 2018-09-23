package modules.Music;

import main.SpecialBot;
import modules.Music.declarations.LoopState;
import modules.Music.declarations.Playlist;
import modules.Music.declarations.Song;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.AudioPlayer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;

public class SpecialAudioPlayer {

    private SpecialBot bot;
    private IGuild guild;
    private AudioPlayer audioPlayer;
    private IChannel lastChannel;
    private long lastAction;

    private Song playing;
    private Queue<Song> songQueue;
    private Queue<Song> songHistory;
    private static final int limit = 25;

    private LoopState loopState = LoopState.OFF;


    public SpecialAudioPlayer(SpecialBot bot, IGuild guild) {
        this.bot = bot;
        this.guild = guild;
        this.audioPlayer = AudioPlayer.getAudioPlayerForGuild(guild);
        this.songQueue = new LinkedList<>();
        this.songHistory = new LinkedList<>();
    }

    /*
        AudioPlayer Extension Methods
     */
    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public void pauseTrack(boolean pause) {
        audioPlayer.setPaused(pause);
    }

    public void skipTrack() {
        audioPlayer.skip();
    }

    public void setVolume(int percent) {
        float volume = (float) (percent) / 100;
        if (volume > 1.5) volume = 1.5f;
        if (volume < 0) volume = 0f;
        audioPlayer.setVolume(volume);
    }

    public int getVolume() {
        float volume = audioPlayer.getVolume();
        return (int) Math.floor(volume * 100);
    }

    public void setSongPosition(long position) {
        if (position > audioPlayer.getCurrentTrack().getCurrentTrackTime()) {
            audioPlayer.getCurrentTrack().fastForwardTo(position);
        } else {
            audioPlayer.getCurrentTrack().rewindTo(position);
        }
    }

    /*
        NEXT
    */

    public void queueSong(Song song) {
        Music.instance.getDownloader().getDownloadThreads().submit(() -> {
            if (!Downloader.isDownloaded(song)) {
                Music.instance.getBot().sendChannelMessage("**Downloading song...**", lastChannel);
                Music.instance.getDownloader().download(song);
            }

        try {
            while(!Downloader.isDownloaded(song)) {
                Thread.sleep(500);
            }
            Thread.sleep(1000);
        } catch (InterruptedException e){
             e.printStackTrace();
        }
        Music.instance.getMusicHandler().onQueue(guild, song);
        songQueue.offer(song);
        if (playing == null) {
            try {
                next();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        });
    }

    public void queuePlaylist(Playlist playlist) {
        Music.instance.getDownloader().getDownloadThreads().submit(() -> {
            if (!playlist.SONGS.stream().allMatch(Downloader::isDownloaded)) { //TODO: Add counter in message that updates the amount of songs downloaded out of total
                Music.instance.getBot().sendChannelMessage("**Downloading songs, This may take a second...**",
                        lastChannel);
            }
            for (Song song : playlist) {
                if (!Downloader.isDownloaded(song)) {
                    Music.instance.getDownloader().download(song);
                }
                songQueue.offer(song);
            }

            Music.instance.getMusicHandler().onQueue(guild, playlist);
            if (playing == null) {
                try {
                    next();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void next() throws IOException, UnsupportedAudioFileException {
        if (playing != null)
            songHistory.offer(playing); //Update the song history regardless of loopState
        if (songHistory.size() > limit) {
            songHistory.poll();
        }
        if (loopState == LoopState.OFF) { //Regular play
            if (songQueue.size() == 0) {
                playing = null;
                return;
            }
            playing = songQueue.poll();
        } else if (loopState == LoopState.SINGLE) {
            //Just don't update the playing variable and reload song below
        } else if (loopState == LoopState.ALL) {
            if (playing != null)
                songQueue.offer(playing); //Add the current song to the end of the queue so that it replays when we finish the other songs
            playing = songQueue.poll();
        }
        File songFile = new File(Music.instance.getMusicDirectory() + File.separator + playing.ID + ".mp3");
        audioPlayer.queue(songFile);
        Music.instance.getMusicHandler().onStart(guild, playing);
        lastAction = System.currentTimeMillis();
    }

    public void shuffleQueue() {
        List<Song> shuffled = new ArrayList<>(songQueue);
        Collections.shuffle(shuffled);
        this.songQueue = new LinkedList<>(shuffled);
    }

    public Queue<Song> getSongQueue() {
        return songQueue;
    }

    public Song getPlaying() {
        return playing;
    }

    public Duration getPlayingPosition() {
        return Duration.ofMillis(audioPlayer.getCurrentTrack().getCurrentTrackTime());
    }

    public Queue<Song> getSongHistory() {
        return songHistory;
    }

    public IChannel getLastChannel() {
        return lastChannel;
    }

    public void setLastChannel(IChannel channel) {
        this.lastChannel = channel;
        this.lastAction = System.currentTimeMillis();
    }

    public void setLoopState(LoopState loopState) {
        this.loopState = loopState;
    }

    public LoopState getLoopState() {
        return loopState;
    }

    public long getLastAction() {
        return lastAction;
    }
}
