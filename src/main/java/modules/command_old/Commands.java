package discord.modules.command;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import discord.modules.audio.AudioManager;
import discord.modules.command.commands.CommandHelp;
import discord.modules.command.commands.CommandTimer;
import discord.modules.database.DatabaseCommands;
import discord.utils.AnnotationUtil;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.modules.IModule;

public class Commands implements IModule {

	private String moduleName = "Commands";
	private String moduleVersion = "1.1";
	private String moduleMinimumVersion = "2.3.0";
	private String author = "SlyVitality";
	private Object handler = new CommandsHandler();
	public static ArrayList<Class<?>> commandClasses = new ArrayList<Class<?>>();
	public static HashMap<CommandA, Method> commands = new HashMap<CommandA, Method>();
	public static IDiscordClient client;

	public boolean enable(IDiscordClient dclient) {
		client = dclient;
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(handler);
		registerCommands();
		registerCommandMethods();
		System.out.println("[Module] Enabled " + moduleName + " V" + moduleVersion + " by " + author);
		return true;
	}

	private static void registerCommands() {
		commandClasses.add(CommandHelp.class);
		commandClasses.add(CommandTimer.class);
		commandClasses.add(AudioManager.class);
		commandClasses.add(DatabaseCommands.class);
	}

	private static void registerCommandMethods() {
		for (Class<?> k : commandClasses) {
			for (Method m : AnnotationUtil.getAnnotatedMethods(k, CommandA.class)) {
				commands.put((CommandA) AnnotationUtil.getAnnotation(m, CommandA.class), m);
			}
		}
	}

	public void disable() {

	}

	public String getAuthor() {
		return author;
	}

	public String getMinimumDiscord4JVersion() {
		return moduleMinimumVersion;
	}

	public String getName() {
		return moduleName;
	}

	public String getVersion() {
		return moduleVersion;
	}

}
