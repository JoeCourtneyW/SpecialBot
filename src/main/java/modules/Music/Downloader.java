package modules.Music;

import com.sapher.youtubedl.YoutubeDL;
import com.sapher.youtubedl.YoutubeDLException;
import com.sapher.youtubedl.YoutubeDLRequest;
import com.sapher.youtubedl.YoutubeDLResponse;
import main.JsonObjects.Playlist;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import utils.LoggerUtil;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Downloader {
    private Music music;
    private ExecutorService downloadThreads;

    public Downloader(Music music) {
        this.music = music;
        downloadThreads = Executors.newFixedThreadPool(5);
    }

    public Playlist.Song DOWNLOAD_YOUTUBE_URL_EXE_WRAPPED(final String url) {

        YoutubeDLRequest request = new YoutubeDLRequest(url, Music.instance.getMusicDirectory().getPath());
        request.setOption("id");
        request.setOption("extract-audio");
        request.setOption("audio-format", "mp3");
        request.setOption("ignore-errors");
        request.setOption("retries", 2);

        try {
            YoutubeDLResponse response = YoutubeDL.execute(request);
            String id = response.getOut().substring(0, response.getOut().length() - 5); //Shaves off .mp3
            File file = new File(Music.instance.getMusicDirectory().getPath() + File.separator + id + ".mp3");
            return new Playlist.Song(id, Music.instance.getYoutubeWrapper().getVideoTitle(id), getFileDuration(file));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ExecutorService getDownloadThreads() {
        return downloadThreads;
    }

    public static File getFile(Playlist.Song song) {
        return new File(Music.instance.getMusicDirectory().getPath() + File.separator + song.ID + ".mp3");
    }

    public long getFileDuration(File file) {
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

    public static boolean isDownloaded(Playlist.Song song) {
        return new File(Music.instance.getMusicDirectory().getPath() + File.separator + song.ID + ".mp3").exists();
    }

    public void download(Playlist.Song song) {
        if (!isDownloaded(song)) {
            downloadThreads.submit(() -> {
                String url = "http://youtu.be/" + song.ID;
                try {
                    Process p = Runtime.getRuntime().exec("sudo youtube-dl --id --extract-audio --audio-format mp3 " + url, null, music.getMusicDirectory());
                    LoggerUtil.DEBUG("Downloading youtube mp3 from " + url);
                    p.waitFor();
                    LoggerUtil.DEBUG("File downloaded");
                } catch (IOException | InterruptedException e) {
                    LoggerUtil.CRITICAL("Exception while trying to download youtube url:" + url);
                }
            });
        }
    }

    public void downloadThroughEXE(Playlist.Song song) {
        if (!isDownloaded(song)) {
            String url = "http://youtu.be/" + song.ID;
            YoutubeDLRequest request = new YoutubeDLRequest(url, Music.instance.getMusicDirectory().getPath());
            request.setOption("id");
            request.setOption("extract-audio");
            request.setOption("audio-format", "mp3");
            request.setOption("ignore-errors");
            request.setOption("retries", 2);

            try {
                YoutubeDL.execute(request);
            } catch (YoutubeDLException e) {
                e.printStackTrace();
            }
        }
    }
}
