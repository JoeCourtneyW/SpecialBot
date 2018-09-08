package main.GuildOptions;

import main.Commands.PermissionLevel;
import main.Main;
import modules.Music.declarations.Playlist;
import modules.Steam.SteamGame;
import sx.blah.discord.handle.obj.IRole;

import java.util.ArrayList;
import java.util.List;

public class GuildOptions {

    public String GUILD_ID;
    public List<Playlist> PLAYLISTS = new ArrayList<>();
    public List<SteamGame> WISHLIST = new ArrayList<>();
    public String PREFIX = ".";
    public int BOT_VOLUME = 100;
    public String DEFAULT_ROLE = "";
    public String MEMBER_ROLE = "";
    public String PRIVILIGED_MEMBER_ROLE = "";
    public String MODERATOR_ROLE = "";
    public String ADMIN_ROLE = "";

    public boolean AUTO_KICK = false;


    public Playlist getPlaylistByName(String name) {
        return PLAYLISTS.stream()
                .filter(playlist -> playlist.NAME.equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public IRole getRoleForPermissionLevel(PermissionLevel permissionLevel) {
        switch (permissionLevel) {
            case MEMBER:
                if (!MEMBER_ROLE.isEmpty())
                    return Main.bot.getClient().getRoleByID(Long.parseLong(MEMBER_ROLE));
                else
                    return null;
            case PRIVILIGED_MEMBER:
                if (!PRIVILIGED_MEMBER_ROLE.isEmpty())
                    return Main.bot.getClient().getRoleByID(Long.parseLong(PRIVILIGED_MEMBER_ROLE));
                else
                    return null;
            case MODERATOR:
                if (!MODERATOR_ROLE.isEmpty())
                    return Main.bot.getClient().getRoleByID(Long.parseLong(MODERATOR_ROLE));
                else
                    return null;
            case ADMIN:
                if (!ADMIN_ROLE.isEmpty())
                    return Main.bot.getClient().getRoleByID(Long.parseLong(ADMIN_ROLE));
                else
                    return null;
            default:
                if (!DEFAULT_ROLE.isEmpty())
                    return Main.bot.getClient().getRoleByID(Long.parseLong(DEFAULT_ROLE));
                else
                    return null;
        }
    }

    public String toString() {
        return GUILD_ID;
    }
}
