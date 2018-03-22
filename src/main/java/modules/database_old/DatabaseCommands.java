package discord.modules.database;

import discord.Main;
import discord.modules.command.Category;
import discord.modules.command.CommandA;
import discord.modules.command.PermissionLevel;
import discord.utils.MessageUtils;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IUser;

public class DatabaseCommands {

	@CommandA(label = "database", name = "Database", permissionLevel=PermissionLevel.ADMINISTRATOR, alias="db",
			category=Category.ADMINISTRATION, description="Change a specific player's database entry")
	public static void dataCommand(IMessage im) {
		MessageUtils.sendChannelMessage("Command not yet setup!", im.getChannel());
		
		return;
	}
	@CommandA(label = "credits", name = "Credits", permissionLevel=PermissionLevel.ADMINISTRATOR, alias="credit",
			category=Category.ADMINISTRATION, description="Change a specific player's database credits entry",
			usage=".credits [add, remove, set, get] <amount> [user]")
	public static void creditsCommand(IMessage im) {
		String[] args = im.getContent().split(" ");
		//!credits [add, remove, set] [amount] [user mention]
		if(!(args.length == 4 || (args.length == 3 && args[1].equalsIgnoreCase("get")))){//commands count in args
			MessageUtils.sendSyntax("Credits", im.getChannel());
			return;
		}
		IUser u;
		if(im.getMentions().size() == 1){
			u = im.getMentions().get(0);
		}else{
			MessageUtils.sendChannelMessage("The final argument must be a single user mention!", im.getChannel());
			return;
		}
		int amt = 0;
		if(!args[1].equalsIgnoreCase("get")){
		try{
			amt = Integer.parseInt(args[2]);
		}catch(NumberFormatException e){
			MessageUtils.sendChannelMessage("The second argument must be a valid number", im.getChannel());
			return;
		}
		}
		int credits = UserValue.CREDITS.getFor(u).asInt();
		MessageUtils.sendChannelMessage("				***" + u.getName() + "***", im.getChannel());
		int newCredits = 0;
		switch(args[1].toLowerCase()){
		case "add":
			newCredits = credits+amt;
			UserValue.CREDITS.setFor(u, newCredits);
			MessageUtils.sendChannelMessage("**Transaction:** " + credits + " + " + amt, im.getChannel());
			MessageUtils.sendChannelMessage("**New Credits:** " + newCredits, im.getChannel());
			Main.databaseModule.syncDatabase(u);
			break;
		case "remove":
			newCredits = credits-amt;
			UserValue.CREDITS.setFor(u, newCredits);
			MessageUtils.sendChannelMessage("**Transaction:** " + credits + " - " + amt, im.getChannel());
			MessageUtils.sendChannelMessage("**New Credits:** " + newCredits, im.getChannel());
			Main.databaseModule.syncDatabase(u);
			break;
		case "set":
			newCredits = amt;
			UserValue.CREDITS.setFor(u, newCredits);
			MessageUtils.sendChannelMessage("**Transaction:** " + "SET " + amt, im.getChannel());
			MessageUtils.sendChannelMessage("**New Credits:** " + newCredits, im.getChannel());
			Main.databaseModule.syncDatabase(u);
			break;
		case "get":
			MessageUtils.sendChannelMessage("**Credits:** " + credits, im.getChannel());
			break;
		}
		return;
	}
}
