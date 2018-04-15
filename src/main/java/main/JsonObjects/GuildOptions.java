package main.JsonObjects;

import java.util.HashMap;
import java.util.List;

public class GuildOptions {

    public String GUILD_ID;
    public String PREFIX = ".";
    public float BOT_VOLUME = 1f;
    public HashMap<String, List<String>> PLAYLISTS = new HashMap<>();
    public String DEFAULT_ROLE = "";
    public boolean AUTO_KICK = false;

    public GuildOptions(String guildID) {
        this.GUILD_ID = guildID;
    }
}
