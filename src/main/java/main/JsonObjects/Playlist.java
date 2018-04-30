package main.JsonObjects;

import java.util.ArrayList;

public class Playlist {
    public String NAME;
    public ArrayList<Song> SONGS;

    public static class Song {
        public String ID;
        public String TITLE;
        public long DURATION = 0;
        //public File FILE; TODO
        public Song() {

        }
        public Song(String id, String title){
            this.ID = id;
            this.TITLE = title;
        }
    }
}
