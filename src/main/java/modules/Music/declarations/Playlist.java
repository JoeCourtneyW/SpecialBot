package modules.Music.declarations;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;

public class Playlist implements Iterable<Song>{
    public String NAME;
    public ArrayList<Song> SONGS;

    @NotNull
    @Override
    public Iterator<Song> iterator() {
        return SONGS.iterator();
    }

}
