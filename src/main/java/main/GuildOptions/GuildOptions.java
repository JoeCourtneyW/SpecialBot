package main.GuildOptions;

import modules.Music.declarations.Playlist;
import modules.Steam.SteamGame;

import java.util.ArrayList;
import java.util.List;

public class GuildOptions {

    public String GUILD_ID;
    public List<Playlist> PLAYLISTS = new ArrayList<>();
    public List<SteamGame> WISHLIST = new ArrayList<>();
    @Modifiable(name = "prefix", validation = Modifiable.InputType.STRING)
    public String PREFIX = ".";
    @Modifiable(name = "volume", validation = Modifiable.InputType.INTEGER)
    public int BOT_VOLUME = 100;
    @Modifiable(name = "defaultRole", validation = Modifiable.InputType.ROLE_MENTION)
    public String DEFAULT_ROLE = "";
    @Modifiable(name = "tempMembership", validation = Modifiable.InputType.BOOLEAN)
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
