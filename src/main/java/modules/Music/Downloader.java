package modules.Music;

import utils.LoggerUtil;

import java.io.File;
import java.io.IOException;

public class Downloader {
    private Music music;
    public Downloader(Music music){
        this.music = music;
    }

    public File downloadYoutubeURL(final String url){

        final String id = YoutubeWrapper.getIdFromUrl(url);

        String path = music.getMusicDirectory().getPath() + File.separator;

        path += id + ".mp3";
        if (new File(path).exists()) {
            return new File(path);
        }
        try {
            Process p = Runtime.getRuntime().exec("sudo youtube-dl --id --extract-audio --audio-format mp3 " + url, null, music.getMusicDirectory());
            LoggerUtil.DEBUG("Downloading youtube mp3 from " + url);
            p.waitFor();
            LoggerUtil.DEBUG("File downloaded");
        } catch (IOException | InterruptedException e) {
            LoggerUtil.CRITICAL("Exception while trying to download youtube url:" + url);
        }

        return new File(path);
    }
}
