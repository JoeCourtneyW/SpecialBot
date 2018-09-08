package main.Commands;

import main.GuildOptions.GuildOptions;
import main.Main;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public enum PermissionLevel {
    ADMIN(null),
    MODERATOR(ADMIN),
    PRIVILIGED_MEMBER(MODERATOR),
    MEMBER(PRIVILIGED_MEMBER),
    DEFAULT(MEMBER);

    private PermissionLevel child;

    PermissionLevel(PermissionLevel child) {
        this.child = child;
    }

    public PermissionLevel getChild() {
        return this.child;
    }

    public boolean hasPermission(IUser user, IGuild guild) {
        if(this == DEFAULT)
            return true;

        GuildOptions options = Main.bot.getGuildOptions(guild);

        PermissionLevel child = this;
        while(child != ADMIN){
            if(options.getRoleForPermissionLevel(child) == null)
                continue;
            if(user.hasRole(options.getRoleForPermissionLevel(child)))
                return true;
            child = child.getChild();
        }
        return false;
    }

}
