package modules.Music;

import javafx.util.Pair;
import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.JsonObjects.GuildOptions;
import main.JsonObjects.Playlist;
import main.SpecialBot;
import sx.blah.discord.util.EmbedBuilder;
import sx.blah.discord.util.audio.AudioPlayer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.regex.Pattern;

public class MusicCommands extends CommandExecutor {
    private Music music;
    private AudioManager audioManager;

    public MusicCommands(SpecialBot bot) {
        super(bot);
        this.music = Music.instance;
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
                            .append(" / *")
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
        if (isURL(event.getArgs()[0]) != null && isYoutubeURL(event.getArgs()[0])) { //The user provided a direct youtube link. We can grab the id from that: no need to search
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
        GuildOptions options = bot.getGuildOptions(event.getGuild());
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

    @Command(label = "playlist", description = "Manage the guild's playlists")
    public void playlistCommand(CommandEvent event) {
        audioManager.setLastChannelControlledFrom(event.getChannel().getGuild(), event.getChannel());
        if (event.getArgs().length < 1) {
            bot.sendChannelMessage("Enter a second argument: [create, list, show, add, remove, delete]", event.getChannel());
            return;
        }
        if (event.getArgs()[0].equalsIgnoreCase("create")) { //Creates a new playlist
            GuildOptions options = bot.getGuildOptions(event.getGuild());
            if (event.getArgs().length > 1) {
                String name = event.getArgsAsString(1);
                if (options.getPlaylistByName(name) != null) {
                    bot.sendChannelMessage("A playlist with that name already exists!", event.getChannel());
                    return;
                }
                Playlist playlist = new Playlist();
                playlist.NAME = name;
                playlist.SONGS = new ArrayList<>();
                options.PLAYLISTS.add(playlist);
                bot.updateGuildOptions(options);
                bot.sendChannelMessage("Created a new playlist: **" + name + "**", event.getChannel());
            } else {
                bot.sendChannelMessage("You must specify a name for the playlist", event.getChannel());
            }
        } else if (event.getArgs()[0].equalsIgnoreCase("list")) { //Lists all playlists on the server
            GuildOptions options = bot.getGuildOptions(event.getGuild());
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Playlists:");
            for (Playlist playlist : options.PLAYLISTS) {
                embedBuilder.appendField(playlist.NAME, playlist.SONGS.size() + " song(s)", true);
            }
            bot.sendEmbed(embedBuilder.build(), event.getChannel());
        } else if (event.getArgs()[0].equalsIgnoreCase("show")) { //Shows all songs on the given playlist
            GuildOptions options = bot.getGuildOptions(event.getGuild());
            if (event.getArgs().length > 1) {
                String name = event.getArgsAsString(1);
                Playlist playlist = options.getPlaylistByName(name);
                if (playlist == null) {
                    bot.sendChannelMessage("No playlist with that name exists", event.getChannel());
                    return;
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.withTitle(playlist.NAME + ":");
                for (Playlist.Song song : playlist.SONGS) {
                    embedBuilder.appendField(song.TITLE, song.DURATION + "", true);
                }
                bot.sendEmbed(embedBuilder.build(), event.getChannel());
            } else {
                bot.sendChannelMessage("You must specify a playlist, type \"" + options.PREFIX + "playlist list\" for a list of playlists", event.getChannel());
            }
        } else if (event.getArgs()[0].equalsIgnoreCase("add")) {
            GuildOptions options = bot.getGuildOptions(event.getGuild());
            if (event.getArgs().length > 1) {
                String args = event.getArgsAsString(1);
                if(!args.contains(":")){
                    bot.sendChannelMessage("Incorrect usage of the command", event.getChannel());
                    return;
                }
                String name = args.split(":")[0];
                String query = args.split(":")[1];
                Playlist playlist = options.getPlaylistByName(name);
                if (playlist == null) {
                    bot.sendChannelMessage("No playlist with that name exists", event.getChannel());
                    return;
                }
                Pair<String, String> video;
                try {
                    video = music.getYoutubeWrapper().searchForVideo(query);
                } catch (IOException e) {
                    bot.sendChannelMessage("An internal error occured while trying to search YouTube with the given query. Contact an administrator", event.getChannel());
                    return;
                }
                playlist.SONGS.add(new Playlist.Song(video.getKey(), video.getValue()));
                bot.updateGuildOptions(options);
                bot.sendChannelMessage("Added **" + video.getValue() + "** to the playlist **" + name + "**", event.getChannel());

            } else {
                bot.sendChannelMessage("You must specify a playlist, type \"" + options.PREFIX + "playlist list\" for a list of playlists", event.getChannel());
            }
        } else if (event.getArgs()[0].equalsIgnoreCase("remove")) {

        } else if (event.getArgs()[0].equalsIgnoreCase("delete")) {

        }
    }

    private URL isURL(String url) {
        try {
            new URL(url);
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    private boolean isYoutubeURL(String url) {
        return Pattern.matches("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$", url);
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
        if (duration.getSeconds() % 60 > 0) {
            format.append(((int) Math.floor(duration.getSeconds())) % 60);
            format.append("s");
        }
        return format.toString();
    }
}
