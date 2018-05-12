package main.JsonObjects;

import java.util.ArrayList;
import java.util.List;

public class GuildOptions {

    public String GUILD_ID;
    public String PREFIX = ".";
    public int BOT_VOLUME = 100;
    public List<Playlist> PLAYLISTS = new ArrayList<>();
    public String DEFAULT_ROLE = "";
    public boolean AUTO_KICK = false;

    public Playlist getPlaylistByName(String name){
        for (Playlist p : PLAYLISTS) {
            if (p.NAME.equalsIgnoreCase(name)) {
                return p;
            }
        }
        return null;
    }

    public String toString(){
        return GUILD_ID;
    }
}
