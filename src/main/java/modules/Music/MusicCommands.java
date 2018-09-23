package modules.Music;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import main.GuildOptions.GuildOptions;
import modules.Music.declarations.LoopState;
import modules.Music.declarations.Playlist;
import modules.Music.declarations.Song;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.util.EmbedBuilder;
import utils.LoggerUtil;
import utils.Pair;
import utils.http.UrlUtil;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class MusicCommands implements CommandExecutor {
    private Music music = Music.instance;

    @Command(label = "queue", description = "Add a song to the song queue", alias = "play")
    public void queueCommand(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        //If they use alias play, with no args, then unpause the song
        if (event.getLabel().equalsIgnoreCase("play") && event.getArgs().length == 0) {
            music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
            music.getAudioPlayer(event.getGuild()).pauseTrack(false);
            event.reply("**Music Unpaused**");
            return;
        }
        if (event.getArgs().length < 1) { //Show current queue if they aren't trying to queue a new song
            StringBuilder queueList = new StringBuilder();
            long totalDuration = 0;
            int counter = 0;

            long trackDuration;

            Song playing = music.getAudioPlayer(event.getGuild()).getPlaying();
            if (playing != null) {

                trackDuration = playing.DURATION;
                queueList.append("Playing: **").append(playing.TITLE).append("** - *")
                        .append(getReadableDuration(music.getAudioPlayer(event.getGuild()).getPlayingPosition())).append("*")
                        .append(" / *")
                        .append(getReadableDuration(Duration.ofMillis(playing.DURATION))).append("*\n");

                totalDuration += (trackDuration - music.getAudioPlayer(event.getGuild()).getPlayingPosition().toMillis());
            }
            for (Song song : music.getAudioPlayer(event.getChannel().getGuild()).getSongQueue()) {
                counter++;

                trackDuration = song.DURATION;
                queueList.append((counter)).append(") **").append(song.TITLE).append("** - *")
                        .append(getReadableDuration(Duration.ofMillis(song.DURATION))).append("*\n");
                totalDuration += trackDuration;
            }

            queueList.append("Total Queue Length: ***").append(getReadableDuration(Duration.ofMillis(totalDuration))).append("***");

            if (totalDuration > 0)
                event.reply(queueList.toString());
            else
                event.reply("*The queue is empty!*");
            return;

        }

        joinVoiceChannel(event.getGuild(), event.getAuthor());

        if (UrlUtil.isUrl(event.getArgs()[0]) && UrlUtil.isYoutubeURL(event.getArgs()[0])) { //The user provided a direct youtube link. We can grab the id from that: no need to search
            String id = YoutubeWrapper.getIdFromUrl(event.getArgs()[0]);
            String title = music.getYoutubeWrapper().getVideoTitle(id);
            long duration = music.getYoutubeWrapper().getVideoDuration(id);
            /*if (duration > 1000 * 60 * 10) {
                event.reply("*That video is too long to play! Videos must be under 10 minutes*");
                return;
            }*/
            music.getAudioPlayer(event.getGuild()).queueSong(new Song(id, title, duration));
        } else { //Search the youtube library and try to find the song provided
            String query = event.getArgsAsString(0);
            Pair<String, String> video;
            try {
                video = music.getYoutubeWrapper().searchForVideo(query);
            } catch (IOException e) {
                event.reply("*An internal error occurred while trying to search YouTube with the given query. Contact an administrator*");
                return;
            }
            long duration = music.getYoutubeWrapper().getVideoDuration(video.getKey());
            if (duration > 1000 * 60 * 10) {
                event.reply("*That video is too long to play! Videos must be under 10 minutes*");
                return;
            }
            music.getAudioPlayer(event.getGuild()).queueSong(new Song(video.getKey(), video.getValue(), duration));
        }


    }

    @Command(label = "history")
    public void history(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());

        StringBuilder historyList = new StringBuilder();
        int counter = 0;
        for (Song song : music.getAudioPlayer(event.getGuild()).getSongHistory()) {
            counter++;
            historyList.append((counter)).append(") **").append(song.TITLE).append("** - *")
                    .append(getReadableDuration(Duration.ofMillis(song.DURATION))).append("*\n");
        }
        if (counter > 0)
            event.reply(historyList.toString());
        else
            event.reply("*No songs have been played*");

    }

    @Command(label = "bring", description = "Brings the bot to the user's current voice channel")
    public void bring(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        if (event.getAuthor().getVoiceStateForGuild(event.getGuild()) != null) { //If the user is in a voice channel
            bot.joinVoiceChannel(event.getAuthor().getVoiceStateForGuild(event.getGuild()).getChannel());
            event.reply("**Joining Voice Channel**");
        } else {
            event.reply("*You are not currently in a voice channel*");
        }
    }

    @Command(label = "disconnect", description = "Remove the bot from the channel")
    public void disconnect(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        IVoiceChannel channel = event.getGuild().getConnectedVoiceChannel();
        if (channel != null) {
            channel.leave();
            event.reply("**Left Voice Channel**");
        } else {
            event.reply("*I am not currently in a voice channel*");

        }
    }

    @Command(label = "rewind", description = "Rewinds the current song to the beginning")
    public void rewind(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        music.getAudioPlayer(event.getGuild()).setSongPosition(0);
        event.reply("**Restarting Song...**");
    }

    @Command(label = "unpause", description = "Unpause the music")
    public void unpause(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        music.getAudioPlayer(event.getGuild()).pauseTrack(false);
        event.reply("**Music Unpaused**");
    }

    @Command(label = "pause", description = "Pause the music")
    public void pauseCommand(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        music.getAudioPlayer(event.getGuild()).pauseTrack(true);
        event.reply("**Music Paused**");
    }

    @Command(label = "skip", description = "Skip the current song")
    public void skipCommand(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        music.getAudioPlayer(event.getGuild()).skipTrack();
        music.getAudioPlayer(event.getGuild()).pauseTrack(false);
        event.reply("**Skipping Song...**");
    }

    @Command(label = "volume", description = "Change the volume of the client")
    public void volumeCommand(CommandEvent event) {
        if (event.getArgs().length == 0) {
            event.reply("**Current Volume: " + music.getAudioPlayer(event.getGuild()).getVolume() + "%**");
            return;
        }
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        try {
            music.getAudioPlayer(event.getGuild()).setVolume(Integer.parseInt(event.getArgs()[0]));
        } catch (NumberFormatException e) {
            event.reply("*Invalid Volume Percentage, Ex: 100*");
            return;
        }
        GuildOptions options = bot.getGuildOptions(event.getGuild());
        options.BOT_VOLUME = music.getAudioPlayer(event.getGuild()).getVolume();
        bot.updateGuildOptions(options);
        event.reply("Set volume to **" + options.BOT_VOLUME + "%**.");
    }

    @Command(label = "loop", description = "Toggle the loop state of the client")
    public void loopCommand(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        switch (music.getAudioPlayer(event.getGuild()).getLoopState()) {
            case OFF:
                music.getAudioPlayer(event.getGuild()).setLoopState(LoopState.SINGLE);
                event.reply("**Now looping current song**");
                break;
            case SINGLE:
                music.getAudioPlayer(event.getGuild()).setLoopState(LoopState.ALL);
                event.reply("**Now looping all songs in the queue**");
                break;
            case ALL:
                music.getAudioPlayer(event.getGuild()).setLoopState(LoopState.OFF);
                event.reply("**No longer looping**");
                break;
        }
    }

    @Command(label = "shuffle", description = "Shuffle the current queue")
    public void shuffleCommand(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).shuffleQueue();
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        event.reply("**Shuffled queue**");
    }

    @Command(label = "playlist", description = "Manage the guild's playlists")
    public void playlistCommand(CommandEvent event) {
        music.getAudioPlayer(event.getGuild()).setLastChannel(event.getChannel());
        if (event.getArgs().length < 1) {
            event.reply("*Enter a second argument: [create, list, play, show, add, remove, delete]*");
            return;
        }
        GuildOptions options = bot.getGuildOptions(event.getGuild());
        if (event.getArgs()[0].equalsIgnoreCase("create")) { //Creates a new playlist
            if (event.getArgs().length > 1) {
                String name = event.getArgsAsString(1);
                if (options.getPlaylistByName(name) != null) {
                    event.reply("*A playlist with that name already exists!*");
                    return;
                }
                Playlist playlist = new Playlist();
                playlist.NAME = name;
                playlist.SONGS = new ArrayList<>();
                options.PLAYLISTS.add(playlist);
                bot.updateGuildOptions(options);
                event.reply("Created a new playlist: **" + name + "**");
            } else {
                event.reply("*You must specify a name for the playlist*");
            }
        } else if (event.getArgs()[0].equalsIgnoreCase("list")) { //Lists all playlists on the server
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.withTitle("Playlists");
            options.PLAYLISTS.forEach(playlist ->
                    embedBuilder.appendField(playlist.NAME, playlist.SONGS.size() + " song(s)", true));
            event.reply(embedBuilder.build());
        } else if (event.getArgs()[0].equalsIgnoreCase("show")) { //Shows all songs on the given playlist
            if (event.getArgs().length > 1) {
                String name = event.getArgsAsString(1);
                Playlist playlist = options.getPlaylistByName(name);
                if (playlist == null) {
                    event.reply("*No playlist with that name exists*");
                    return;
                }
                EmbedBuilder embedBuilder = new EmbedBuilder();
                int counter = 1;
                long totalDuration = 0;
                for (Song song : playlist.SONGS) {
                    embedBuilder.appendField(counter + ") " + song.TITLE, getReadableDuration(Duration.ofMillis(song.DURATION)), true);
                    counter++;
                    totalDuration += song.DURATION;
                }
                embedBuilder.withTitle(playlist.NAME + " (" + getReadableDuration(Duration.ofMillis(totalDuration)) + ")");
                event.reply(embedBuilder.build());
            } else {
                event.reply(
                        "*You must specify a playlist, type \"" + options.PREFIX + "playlist list\" for a list of playlists*");
            }
        } else if (event.getArgs()[0].equalsIgnoreCase("add")) {
            if (event.getArgs().length > 1) {
                String args = event.getArgsAsString(1);
                if (!args.contains(":")) {
                    event.reply("*Incorrect usage of the command, 'playlist add [playlist]:[song name]'*");
                    return;
                }
                String name = args.split(":")[0];
                String query = args.split(":")[1];

                Playlist playlist = options.getPlaylistByName(name);
                if (playlist == null) {
                    event.reply("*No playlist with that name exists*");
                    return;
                }

                Pair<String, String> video;
                try {
                    video = music.getYoutubeWrapper().searchForVideo(query);
                } catch (IOException e) {
                    event.reply("*An internal error occured while trying to search YouTube with the given query*");
                    return;
                }

                playlist.SONGS.add(new Song(video.getKey(), video.getValue(), music.getYoutubeWrapper().getVideoDuration(video.getKey())));

                bot.updateGuildOptions(options);
                event.reply("Added **" + video.getValue() + "** to the playlist **" + name + "**");

            } else {
                event.reply(
                        "*You must specify a playlist, type \"" + options.PREFIX + "playlist list\" for a list of playlists*");
            }
        } else if (event.getArgs()[0].equalsIgnoreCase("remove")) {
            if (event.getArgs().length > 1) {
                String args = event.getArgsAsString(1);
                if (!(args.length() > 2)) {
                    event.reply("*Incorrect usage of the command, 'playlist remove [playlist] [song index]'*");
                    return;
                }
                String playlistName = event.getArgs()[1];
                int songIndex;
                try {
                    songIndex = Integer.parseInt(event.getArgs()[2]);
                } catch (NumberFormatException e) {
                    event.reply("*Song index must be a integer*");
                    return;
                }
                Playlist playlist = options.getPlaylistByName(playlistName);
                if (playlist == null) {
                    event.reply("*No playlist with that name exists*");
                    return;
                }
                if (playlist.SONGS.size() < songIndex) {
                    event.reply("*You must choose an index within the playlist size*");
                    return;
                }
                Song song = playlist.SONGS.get(songIndex);
                playlist.SONGS.remove(songIndex - 1);
                bot.updateGuildOptions(options);
                event.reply("Removed **" + song.TITLE + "** from the playlist **" + playlist.NAME + "**");
            } else {
                event.reply(
                        "*You must specify a playlist, type \"" + options.PREFIX + "playlist list\" for a list of playlists*");
            }
        } else if (event.getArgs()[0].equalsIgnoreCase("delete")) {
            if (event.getArgs().length > 1) {
                String name = event.getArgsAsString(1);
                Playlist playlist = options.getPlaylistByName(name);
                if (playlist == null) {
                    event.reply("*No playlist with that name exists*");
                    return;
                }
                List<Playlist> copiedList = new ArrayList<>(options.PLAYLISTS);
                copiedList.stream()
                        .filter(toDelete -> playlist.NAME.equalsIgnoreCase(toDelete.NAME))
                        .forEach(options.PLAYLISTS::remove);
                bot.updateGuildOptions(options);
                event.reply("*You have successfully deleted* **" + playlist.NAME + "**");
            } else {
                event.reply(
                        "*You must specify a playlist, type \"" + options.PREFIX + "playlist list\" for a list of playlists*");
            }
        } else if (event.getArgs()[0].equalsIgnoreCase("play")) {
            if (event.getArgs().length > 1) {
                String name = event.getArgsAsString(1);
                Playlist playlist = options.getPlaylistByName(name);
                if (playlist == null) {
                    event.reply("*No playlist with that name exists*");
                    return;
                }
                joinVoiceChannel(event.getGuild(), event.getAuthor());
                music.getAudioPlayer(event.getGuild()).queuePlaylist(playlist);
            } else {
                event.reply(
                        "*You must specify a playlist, type \"" + options.PREFIX + "playlist list\" for a list of playlists*");
            }
        } else {
            event.reply("*Enter a second argument: [create, list, play, show, add, remove, delete]*");
        }
    }

    public void joinVoiceChannel(IGuild guild, IUser user) {
        try {
            if (bot.getClient().getConnectedVoiceChannels().size() == 0) { //Make sure to join the voice channel before trying to play
                if (user.getVoiceStateForGuild(guild) != null) //If the user is in a voice channel
                    bot.joinVoiceChannel(user.getVoiceStateForGuild(guild).getChannel());
                else
                    bot.joinVoiceChannel(guild.getVoiceChannels().get(0)); //If the user isn't connected to a voice channel, join the (presumably) lobby
            }
        } catch (NullPointerException e) {
            LoggerUtil.CRITICAL("Failed to join user's voice channel");
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
        if (duration.getSeconds() % 60 > 0) {
            format.append(((int) Math.floor(duration.getSeconds())) % 60);
            format.append("s");
        }
        return format.toString();
    }
}
