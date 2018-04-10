package discord.modules.audio;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import org.tritonus.share.sampled.file.TAudioFileFormat;

import discord.Main;
import discord.modules.command.Category;
import discord.modules.command.CommandA;
import discord.modules.command.PermissionLevel;
import discord.utils.MessageUtils;
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

public class AudioManager {


	//TODO Playlists, Download song only
	static ConcurrentHashMap<IGuild, IChannel> lastChannel = new ConcurrentHashMap<IGuild, IChannel>();
	@Command(label="queue", description="Add a song to the song queue"	)
	public static void queueCommand(IMessage im) {
		lastChannel.put(im.getGuild(), im.getChannel());
		String[] split = im.getContent().split(" ");
		String[] args = split.length > 1 ? Arrays.copyOfRange(split, 1, split.length) : new String[0];
		if(args.length < 1){
			StringBuilder sb = new StringBuilder();
			long total = 0;
			for(int i = 0; i < getPlayer(im.getGuild()).getPlaylistSize(); i++){
				AudioPlayer.Track track = getPlayer(im.getGuild()).getPlaylist().get(i);
				File f = (File) track.getMetadata().get("file");
				long dur = 0;
				try{
				dur = getDuration(f);
				}catch(Exception e){
					MessageUtils.sendChannelMessage("An internal error occured (IOE)", im.getChannel());
				}

				if(i == 0){
					sb.append("Playing: **" + getTrackTitle(track) + "** - *" + getCurrentTrackTime(track) +"*\n");
					total += (dur-track.getCurrentTrackTime());
				}else{
					sb.append((i) + ") **" + getTrackTitle(track) + "** - *" + getTrackLength(track) +"*\n");
					total += dur;
				}
				}
			sb.append("Total Queue Length: ***" + convertMilli(total) + "***");
			if(total > 0)
				MessageUtils.sendChannelMessage(sb.toString(), im.getChannel());
			else
				MessageUtils.sendChannelMessage("The queue is empty!", im.getChannel());
			return;

		}
		if(Main.INSTANCE.client.getConnectedVoiceChannels().size() == 0){
			try {
				join(im.getChannel(), im.getAuthor());
			} catch (RateLimitException e) {
				MessageUtils.sendChannelMessage("An internal error occured (RLE)", im.getChannel());
			} catch (DiscordException e) {
				MessageUtils.sendChannelMessage("An internal error occured (DE)", im.getChannel());
			} catch (MissingPermissionsException e) {
				MessageUtils.sendChannelMessage("An internal error occured (MPE)", im.getChannel());
			}
		}
		if(isURL(args[0])){
			try {
				queueYoutube(im.getChannel(), args[0], "");
			} catch (RateLimitException e) {
				MessageUtils.sendChannelMessage("An internal error occured (RLE)", im.getChannel());
			} catch (DiscordException e) {
				MessageUtils.sendChannelMessage("An internal error occured (DE)", im.getChannel());
			} catch (MissingPermissionsException e) {
				MessageUtils.sendChannelMessage("An internal error occured (MPE)", im.getChannel());
			}
		}else{
			StringBuilder sb = new StringBuilder();
			for(String a : args){
				sb.append(a + " ");
			}
			sb.setLength(sb.length()-1);
			String id = "";
			String title = "";
			try {
				String[] data = discord.modules.audio.Search.getVideoID(sb.toString());
				id = data[0];
				title = data[1];
			} catch (IOException e) {
				MessageUtils.sendChannelMessage("An internal error occured (IOE)", im.getChannel());
			}
			String url = "https://www.youtube.com/watch?v=" + id;
			try {
				queueYoutube(im.getChannel(), url, title);
			} catch (RateLimitException e) {
				MessageUtils.sendChannelMessage("An internal error occured (RLE)", im.getChannel());
			} catch (DiscordException e) {
				MessageUtils.sendChannelMessage("An internal error occured (DE)", im.getChannel());
			} catch (MissingPermissionsException e) {
				MessageUtils.sendChannelMessage("An internal error occured (MPE)", im.getChannel());
			}
		}
			//MessageUtils.sendChannelMessage("That is not a valid Youtube URL!", im.getChannel());
		
		
	}
	private static boolean isURL(String url){
		try{
			new URL(url);
			return true;
		}catch(MalformedURLException e){
			return false;
		}
	}
	@CommandA(label="unpause", name="Unpause", category=Category.MUSIC, alias="resume",
			permissionLevel=PermissionLevel.MODERATOR, description="Unpause the music"		)
	public static void playCommand(IMessage im) {
		lastChannel.put(im.getGuild(), im.getChannel());
		if(Main.INSTANCE.client.getConnectedVoiceChannels().size() == 0){
			try {
				join(im.getChannel(), im.getAuthor());
			} catch (RateLimitException e) {
				MessageUtils.sendChannelMessage("An internal error occured (RLE)", im.getChannel());
			} catch (DiscordException e) {
				MessageUtils.sendChannelMessage("An internal error occured (DE)", im.getChannel());
			} catch (MissingPermissionsException e) {
				MessageUtils.sendChannelMessage("An internal error occured (MPE)", im.getChannel());
			}
		}
		pause(im.getChannel(), false);
	}
	@CommandA(label="pause", name="Pause", category=Category.MUSIC, alias="stop",
			permissionLevel=PermissionLevel.MODERATOR, description="Pause the music"		)
	public static void pauseCommand(IMessage im){
		lastChannel.put(im.getGuild(), im.getChannel());
		pause(im.getChannel(), true);
	}
	@CommandA(label="skip", name="Skip", category=Category.MUSIC,
			permissionLevel=PermissionLevel.MODERATOR, description="Skip the current song"		)
	public static void skipCommand(IMessage im){
		lastChannel.put(im.getGuild(), im.getChannel());
		skip(im.getChannel());
		pause(im.getChannel(), false);
	}
	@CommandA(label="volume", name="Volume", category=Category.MUSIC, alias="vol",
			permissionLevel=PermissionLevel.MODERATOR, description="Change the volume of the client"		)
	public static void volumeCommand(IMessage im){
		lastChannel.put(im.getGuild(), im.getChannel());
		try {
			volume(im.getChannel(), Integer.parseInt(im.getContent().split(" ")[1]));
		} catch (Exception e) {
			MessageUtils.sendChannelMessage("Invalid Volume Percentage", im.getChannel());
		}
	}
	/*
	Track events
	 */


	@EventSubscriber
	public void onTrackQueue(final TrackQueueEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
		final IGuild guild = event.getPlayer().getGuild();
		Timer t = new Timer();
		t.schedule(new TimerTask(){
			public void run(){
				try {
					lastChannel.get(guild).sendMessage("Added **" + getTrackTitle(event.getTrack()) + "** to the queue.");
				} catch (RateLimitException e) {
					MessageUtils.sendChannelMessage("An internal error occured (RLE)", lastChannel.get(guild));
				} catch (DiscordException e) {
					MessageUtils.sendChannelMessage("An internal error occured (DE)", lastChannel.get(guild));
				} catch (MissingPermissionsException e) {
					MessageUtils.sendChannelMessage("An internal error occured (MPE)", lastChannel.get(guild));
				}
			}
		}, 500);
	}

	@EventSubscriber
	public void onTrackStart(TrackStartEvent event) throws RateLimitException, DiscordException, MissingPermissionsException {
		IGuild guild = event.getPlayer().getGuild();
		lastChannel.get(guild).sendMessage("Now playing **" + getTrackTitle(event.getTrack()) + "**.");
	}

	/*
	Audio player methods
	 */

	public static void join(IChannel channel, IUser user) throws RateLimitException, DiscordException, MissingPermissionsException {
		if (user.getConnectedVoiceChannels().size() < 1)
			channel.sendMessage("You aren't in a voice channel!");
		else {
			IVoiceChannel voice = user.getConnectedVoiceChannels().get(0);
			if (!voice.getModifiedPermissions(Main.INSTANCE.client.getOurUser()).contains(Permissions.VOICE_CONNECT))
				channel.sendMessage("I can't join that voice channel!");
			else if (voice.getConnectedUsers().size() >= voice.getUserLimit() && voice.getUserLimit() > 0)
				channel.sendMessage("That room is full!");
			else {
				voice.join();
				//channel.sendMessage("Connected to **" + voice.getName() + "**.");
			}
		}
	}
	public static void queueYoutube(IChannel channel, String url, String title) throws RateLimitException, DiscordException, MissingPermissionsException {
		String[] data = downloadYoutubeURL(url, title, channel.getGuild());
		String filePath = data[0];
		String id = data[1];
		if(id.equalsIgnoreCase(""))
			return;
		filePath = filePath + id + ".mp3";
		File f = new File(filePath);
		if (!f.exists())
			channel.sendMessage("That file doesn't exist!");
		else if (!f.canRead())
			channel.sendMessage("I don't have access to that file!");
		else {
			try {
				Track t = getPlayer(channel.getGuild()).queue(f);
				setTrackTitle(t, title);
				setTrackFile(t, f);
			} catch (IOException e) { 
				channel.sendMessage("An IO exception occured: " + e.getMessage());
			} catch (UnsupportedAudioFileException e) {
				channel.sendMessage("That type of file is not supported!");
			}
		}
	}

	public static void pause(IChannel channel, boolean pause) {
		getPlayer(channel.getGuild()).setPaused(pause);
	}

	public static void skip(IChannel channel) {
		getPlayer(channel.getGuild()).skip();
	}

	public static void volume(IChannel channel, int percent) throws RateLimitException, DiscordException, MissingPermissionsException {
		volume(channel, (float) (percent) / 100);
	}

	private static void volume(IChannel channel, Float vol) throws RateLimitException, DiscordException, MissingPermissionsException {
		if (vol > 1.5) vol = 1.5f;
		if (vol < 0) vol = 0f;
		getPlayer(channel.getGuild()).setVolume(vol);
		channel.sendMessage("Set volume to **" + (int) (vol * 100) + "%**.");
	}

	/*
	Utility methods
	 */

	private static AudioPlayer getPlayer(IGuild guild) {
		return AudioPlayer.getAudioPlayerForGuild(guild);
	}

	private static String getTrackTitle(AudioPlayer.Track track) {
		return track.getMetadata().containsKey("title") ? String.valueOf(track.getMetadata().get("title")) : "Unknown Track";
	}
	private static String getTrackLength(AudioPlayer.Track track) {
		File f = (File) track.getMetadata().get("file");
		long dur = 0;
		try{
		dur = getDuration(f);
		}catch(Exception e){
			
		}
		return String.format("%dm %02ds", 
			    TimeUnit.MILLISECONDS.toMinutes(dur),
			    TimeUnit.MILLISECONDS.toSeconds(dur) % 60
			);
	}
	private static String getCurrentTrackTime(AudioPlayer.Track track) {
		File f = (File) track.getMetadata().get("file");
		long dur = 0;
		try{
		dur = getDuration(f);
		}catch(Exception e){
			
		}
		return String.format("%dm %02ds / %dm %02ds", 
				TimeUnit.MILLISECONDS.toMinutes(track.getCurrentTrackTime()),
			    TimeUnit.MILLISECONDS.toSeconds(track.getCurrentTrackTime())% 60,
			    TimeUnit.MILLISECONDS.toMinutes(dur),
			    TimeUnit.MILLISECONDS.toSeconds(dur)% 60
			);
	}
	private static String convertMilli(long milli){
		return String.format("%dm %02ds", 
			    TimeUnit.MILLISECONDS.toMinutes(milli),
			    TimeUnit.MILLISECONDS.toSeconds(milli) % 60
			);
	}
	private static void setTrackTitle(AudioPlayer.Track track, String title) {
		track.getMetadata().put("title", title);
	}
	private static void setTrackFile(AudioPlayer.Track track, File f){
		track.getMetadata().put("file", f);
	}
	private static long getDuration(File file) throws UnsupportedAudioFileException, IOException {

	    AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
	    if (fileFormat instanceof TAudioFileFormat) {
	        Map<?, ?> properties = ((TAudioFileFormat) fileFormat).properties();
	        String key = "duration";
	        Long microseconds = (Long) properties.get(key);
	        return microseconds/1000;
	    } else {
	        throw new UnsupportedAudioFileException();
	    }

	}
	private static boolean downloading = false;
	private static String[] downloadYoutubeURL(final String url, String title, IGuild g){

		final String path = "/music/";
		String id = "";
		if(url.contains("youtu.be")){
			id = url.split("/")[1];
		}else{
			id = url.split("\\?v=")[1];
			if(id.contains("&"))
				id = id.split("&")[0];
		}
		String[] r = new String[2];
		r[0] = path;
		r[1] = id; 
		if(new File(path + id + ".mp3").exists()){
			return r;
		}
		if(downloading){
			MessageUtils.sendChannelMessage("I'm already downloading a file! Please wait", lastChannel.get(g));
			r[1] = "";
			return r;
		}
			
		MessageUtils.sendChannelMessage("Downloading file...", lastChannel.get(g));

			Thread dlThread = new Thread(){
				public void run(){
				try {
				downloading = true;
				Process p = Runtime.getRuntime().exec("sudo youtube-dl --id --extract-audio --audio-format mp3 " + url, null, new File("/music"));
				InputStream is = p.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String line;
				String nPath = path;
				while((line=br.readLine())!=null){
					if(line.startsWith("[ffmpeg]")){
						nPath += line.split(": ")[1];
					}
				}
				p.waitFor();
				Runtime.getRuntime().exec("sudo chmod 777 " + nPath);
				downloading = false;
			} catch (IOException e) {
				downloading = false;
			} catch (InterruptedException e) {
				downloading = false;
			}
				}
		    };
			dlThread.start();
			try {
				dlThread.join();
			} catch (InterruptedException e) {
				MessageUtils.sendChannelMessage("An internal error occured (IE)", lastChannel.get(g));
				e.printStackTrace();
			}
		//MessageUtils.sendChannelMessage(path, lastChannel.get(g));
		
		return r;
	}
}
