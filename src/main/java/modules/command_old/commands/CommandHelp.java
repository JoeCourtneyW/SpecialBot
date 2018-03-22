package discord.modules.command.commands;

import discord.modules.command.Category;
import discord.modules.command.CommandA;
import discord.modules.command.Commands;
import discord.utils.MessageUtils;
import sx.blah.discord.handle.obj.IMessage;

public class CommandHelp{

	@CommandA(label = "help", name = "Help", description="List all commands on the server")
	public static void helpCommand(IMessage im) {
		String message = "```";
		for (Category cat : Category.values()) {
			message += cat.getFormal() + ": \n";
			for (CommandA ca : Commands.commands.keySet()) {
				if(ca.category() != cat)
					continue;
				String spacer = "";
				for (int i = 0; i < 15 - ca.label().length(); i++)
					spacer += " ";
				message += "  " + ca.label() + spacer + ca.description() + "\n";
			}
		}
		/*String g = ":flag_de:";        
		String s = "       ";              
		String br = "\n";
		MessageUtils.sendChannelMessage(g+s+g+g+g+br + g+s+g+s+s+br + 
				g+g+g+g+g+br + s+s+""+g+s+g+br + g+g+g+s+g+br, im.getChannel());*/
		
		MessageUtils.sendPrivateMessage(message + "```", im.getAuthor());
		return;
	}

}
