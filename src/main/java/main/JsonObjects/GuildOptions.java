package main.JsonObjects;

import java.util.HashMap;
import java.util.List;

public class GuildOptions {

    public String GUILD_ID;
    public String PREFIX = ".";
    public int BOT_VOLUME = 100;
    public HashMap<String, List<String>> PLAYLISTS = new HashMap<>();
    public String DEFAULT_ROLE = "";
    public boolean AUTO_KICK = false;

    public String toString(){
        return GUILD_ID;
    }
}
