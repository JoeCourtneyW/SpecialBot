package discord.modules.database;

import discord.Main;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.UserJoinEvent;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IUser;

public class DatabaseListener{


	@EventSubscriber
	public void newPlayerOnJoin(UserJoinEvent event) {
		Main.databaseModule.tryNewPlayer(event.getUser());
	}
	public static void oneTimeOnly(){
		for(IGuild g : Main.INSTANCE.client.getGuilds()){
			for(IUser u : g.getUsers()){
				Main.databaseModule.tryNewPlayer(u);
			}
		}
	}
}
