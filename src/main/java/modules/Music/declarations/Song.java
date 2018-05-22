package modules.Music.declarations;

public class Song {
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
