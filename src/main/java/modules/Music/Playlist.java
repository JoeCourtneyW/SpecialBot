package modules.Music;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class Playlist implements Iterable<Playlist.Song>{
    public String NAME;
    public ArrayList<Song> SONGS;

    @NotNull
    @Override
    public Iterator<Song> iterator() {
        return SONGS.iterator();
    }

    public static class Song {
        public String ID;
        public String TITLE;
        public long DURATION = 0;
        public Song() {

        }
        public Song(String id, String title, long duration){
            this.ID = id;
            this.TITLE = title;
            this.DURATION = duration;
        }
    }
}
