package modules.Music;

import main.SpecialBot;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.audio.events.*;

import java.util.Timer;
import java.util.TimerTask;

public class MusicHandler {
    private Music music;
    private SpecialBot bot;

    public MusicHandler(SpecialBot bot) {
        this.bot = bot;
        this.music = (Music) bot.getModule("Music");
    }
	/*
	Track events
	 */

    @EventSubscriber
    public void onTrackQueue(final TrackQueueEvent event) {
        final IGuild guild = event.getPlayer().getGuild();


        new Timer().schedule( //Have to delay this track queueing to wait for the track data to be set properly
                new TimerTask() {
                    public void run() {
                        if (music.getAudioManager().getAudioPlayer(guild).getCurrentTrack().getMetadata().get("title")
                                .equals(event.getTrack().getMetadata().get("title")))
                            return;
                        bot.sendChannelMessage("Added **" + music.getAudioManager().getTrackTitle(event.getTrack()) + "** to the queue.",
                                music.getAudioManager().getLastChannelControlledFrom(event.getPlayer().getGuild()));
                    }
                }, 200);
    }

    @EventSubscriber
    public void onTrackStart(TrackStartEvent event) {
        IGuild guild = event.getPlayer().getGuild();
        new Timer().schedule( //waitUntilTrackDataIsSet
                new TimerTask() {
                    public void run() {
                        bot.sendChannelMessage("Now playing **" + music.getAudioManager().getTrackTitle(event.getTrack()) + "**.",
                                music.getAudioManager().getLastChannelControlledFrom(guild));
                    }
                }, 200);
    }

    @EventSubscriber
    public void onVolume(VolumeChangeEvent event) {
        if(music.getAudioManager().getLastChannelControlledFrom(event.getPlayer().getGuild()) != null) {
            bot.sendChannelMessage("Set volume to **" + (int) (event.getNewValue() * 100) + "%**.",
                    music.getAudioManager().getLastChannelControlledFrom(event.getPlayer().getGuild()));
        }
    }

    @EventSubscriber
    public void onSkip(TrackSkipEvent event) {
        bot.sendChannelMessage("**Skipping Current Track**",
                music.getAudioManager().getLastChannelControlledFrom(event.getPlayer().getGuild()));
    }

    @EventSubscriber
    public void onLoop(LoopStateChangeEvent event) {
        bot.sendChannelMessage("**" + (event.getNewLoopState() ? "Now looping current song" : "No longer looping") + "**",
                music.getAudioManager().getLastChannelControlledFrom(event.getPlayer().getGuild()));
    }

    @EventSubscriber
    public void onShuffle(ShuffleEvent event) {
        bot.sendChannelMessage("**Queue shuffled**",
                music.getAudioManager().getLastChannelControlledFrom(event.getPlayer().getGuild()));
    }
}
