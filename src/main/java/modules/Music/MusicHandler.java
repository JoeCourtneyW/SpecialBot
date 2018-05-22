package modules.Music;

import main.SpecialBot;
import modules.Music.declarations.Playlist;
import modules.Music.declarations.Song;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.events.TrackFinishEvent;
import sx.blah.discord.util.audio.events.TrackSkipEvent;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;

public class MusicHandler {
    private Music music;
    private SpecialBot bot;

    public MusicHandler(SpecialBot bot) {
        this.bot = bot;
        this.music = Music.instance;
    }

    /*
        SpecialAudioPlayer handlers
     */
    @EventSubscriber
    public void onFinishNext(TrackFinishEvent event) throws UnsupportedAudioFileException, IOException {
        IGuild guild = event.getPlayer().getGuild();
        music.getAudioPlayer(guild).next();
    }

    @EventSubscriber
    public void onSkipNext(TrackSkipEvent event) throws UnsupportedAudioFileException, IOException  {
        IGuild guild = event.getPlayer().getGuild();
        music.getAudioPlayer(guild).next();
    }

    //NON DISCORD4J HANDLERS
    public void onQueue(IGuild guild, Playlist playlist){
            bot.sendChannelMessage("Added **" + playlist.NAME + "** to the queue. *(" + playlist.SONGS.size() + " songs)*",
                    music.getAudioPlayer(guild).getLastChannel());
    }
    public void onQueue(IGuild guild, Song song){
        if(music.getAudioPlayer(guild).getPlaying() != null) {
            bot.sendChannelMessage("Added **" + song.TITLE + "** to the queue.",
                    music.getAudioPlayer(guild).getLastChannel());
        }
    }

    public void onStart(IGuild guild, Song song){
        bot.sendChannelMessage("Now playing **" + song.TITLE + "**.",
                music.getAudioPlayer(guild).getLastChannel());
    }
}
