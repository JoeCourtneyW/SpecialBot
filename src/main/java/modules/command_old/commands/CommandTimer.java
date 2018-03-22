package discord.modules.command.commands;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import discord.modules.command.Category;
import discord.modules.command.CommandA;
import discord.utils.MessageUtils;
import sx.blah.discord.handle.obj.IMessage;

public class CommandTimer{

	@SuppressWarnings("deprecation")
	@CommandA(label = "timer", name = "Timer",
	description = "Starts a reminder to be executed at the given time", category = Category.GENERAL,
	usage=".timer <alarm, countdown> <timer_value> [name]")
	public static boolean execute(final IMessage im) {
		final String[] args = im.getContent().split(" "); // ARGS[0] RETURNS THE
															// COMMAND
		// must index by 1 to ignore command
		if (args.length < 3) {
			MessageUtils.sendSyntax("Timer", im.getChannel());
			return false;
		}
		if (args[1].equalsIgnoreCase("Alarm")) {
			String time = args[2].toLowerCase();
			final String name;
			if(args.length == 4)
				name = args[3];
			else
				name = "";
			int minute = Calendar.getInstance().getTime().getMinutes();
			String min = minute < 10 ? "0" + minute : minute + "";
			int hour = Calendar.getInstance().getTime().getHours();
			String ho = hour < 10 ? "0" + hour : hour + "";
			final String curTime = ho + ":" + min;
			if (time.contains(":")) {
				int hours;
				int minutes;
				try{
				hours = Integer.parseInt(time.split(":")[0]);
				minutes = Integer.parseInt(time.split(":")[1]);
				}catch(NumberFormatException e){
					MessageUtils.sendSyntax("Timer", im.getChannel());
					return false;
				}
				Date nDate;
				Calendar c = Calendar.getInstance();
				c.set(Calendar.HOUR_OF_DAY, hours);
				c.set(Calendar.MINUTE, minutes);
				c.set(Calendar.SECOND, 0);
				if(hours < hour && minutes < minute){
					c.set(Calendar.DATE, c.get(Calendar.DATE) + 1);
				}
				nDate = c.getTime();
				MessageUtils.sendChannelMessage("Successfully set an alarm for " + time, im.getChannel());
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						if (args.length == 4) {
							MessageUtils.sendPrivateMessage("Your *" + name + "* alarm from " + curTime + " has gone off!",
									im.getAuthor());
						} else {
							MessageUtils.sendPrivateMessage("Your alarm from " + curTime + " has gone off!", im.getAuthor());

						}
					}
				}, nDate);
			} else {
				MessageUtils.sendSyntax("Timer", im.getChannel());
				return false;
			}
		} else if (args[1].equalsIgnoreCase("Countdown")) {
			String time = args[2].toLowerCase();
			final String name;
			if(args.length == 4)
				name = args[3];
			else
				name = "";
			int lastSplit = 0;
			int minute = Calendar.getInstance().getTime().getMinutes();
			String min = minute < 10 ? "0" + minute : minute + "";
			int hour = Calendar.getInstance().getTime().getHours();
			String ho = hour < 10 ? "0" + hour : hour + "";
			final String curTime = ho + ":" + min;
			ArrayList<String> separate = new ArrayList<String>();
			if (time.contains("s") || time.contains("m") || time.contains("h")) {
				for (int i = 0; i < time.length(); i++) {
					if (time.charAt(i) == 's' || time.charAt(i) == 'm' || time.charAt(i) == 'h') {
						separate.add(time.substring(lastSplit, i + 1));
						lastSplit = i + 1;
					}
				}
				long timer = 0;

				for (String s : separate) {
					try {
						if (s.contains("s")) {
							timer += Integer.parseInt(s.substring(0, s.length() - 1)) * 1000;
						} else if (s.contains("m")) {
							timer += Integer.parseInt(s.substring(0, s.length() - 1)) * 1000 * 60;
						} else if (s.contains("h")) {
							timer += Integer.parseInt(s.substring(0, s.length() - 1)) * 1000 * 60 * 60;
						}
					} catch (NumberFormatException e) {
						MessageUtils.sendSyntax("Timer", im.getChannel());
						return false;
					}
				}
				MessageUtils.sendChannelMessage("Successfully started a timer for " + time, im.getChannel());
				new Timer().schedule(new TimerTask() {
					@Override
					public void run() {
						if (args.length == 4) {
							MessageUtils.sendPrivateMessage("Your *" + name + "* timer from " + curTime + " is up!",
									im.getAuthor());
						} else {
							MessageUtils.sendPrivateMessage("Your timer from " + curTime + " is up!", im.getAuthor());

						}
					}
				}, timer);
			} else {
				MessageUtils.sendSyntax("Timer", im.getChannel());
				return false;
			}
		} else {
			MessageUtils.sendSyntax("Timer", im.getChannel());
			return false;
		}
		return false;
	}

}
