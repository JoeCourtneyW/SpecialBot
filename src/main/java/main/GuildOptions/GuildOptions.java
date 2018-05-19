package main.GuildOptions;

import modules.Music.Playlist;
import modules.Steam.SteamGame;

import java.util.ArrayList;
import java.util.List;

public class GuildOptions {

    public String GUILD_ID;
    public List<Playlist> PLAYLISTS = new ArrayList<>();
    public List<SteamGame> WISHLIST = new ArrayList<>();
    @Modifiable(name = "prefix")
    public String PREFIX = ".";
    @Modifiable(name = "volume")
    public int BOT_VOLUME = 100;
    @Modifiable(name = "defaultRole")
    public String DEFAULT_ROLE = "";
    @Modifiable(name = "tempMembership")
    public boolean AUTO_KICK = false;

    public Playlist getPlaylistByName(String name) {
        return PLAYLISTS.stream()
                .filter(playlist -> playlist.NAME.equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public String toString() {
        return GUILD_ID;
    }
}
