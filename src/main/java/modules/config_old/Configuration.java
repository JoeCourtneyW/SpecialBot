package discord.modules.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.modules.IModule;

public class Configuration implements IModule {
	private String moduleName = "Configuration";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.3.0";
	private String author = "SlyVitality";
	private HashMap<String, String> configValues = new HashMap<String, String>();
	private HashMap<Integer, String> comments = new HashMap<Integer, String>();
	File f = new File("/home/sly/CynicalBot/config.sly");

	public static IDiscordClient client;

	public boolean enable(IDiscordClient dclient) {
		client = dclient;
		createConfig();
		readConfig();
		System.out.println("[Module] Enabled " + moduleName + " V" + moduleVersion + " by " + author);
		rewriteConfig();
		return true;
	}

	public void disable() {
		rewriteConfig();
	}

	private void createConfig() {
		if (!f.exists()) {
			try {
				f.createNewFile();
				PrintWriter pw = new PrintWriter(new FileWriter(f));
				pw.write("PREFIX: '!'\n" + "ADMIN_ROLE: 'BotAdmin'\n" + "MODERATOR_ROLE: 'BotMod'\n" + "DATABASE_USERNAME: 'root'\n" + "DATABASE_PASSWORD: 'password'\n");
				pw.close();
			} catch (IOException e) {
			}
		}

	}

	private void readConfig() {
		try {
			BufferedReader in = new BufferedReader(new FileReader(f));
			String line;
			int lineNum = 0;
			while ((line = in.readLine()) != null) {
				lineNum++;
				if (!line.startsWith("#")) // start comments with #
					configValues.put(line.split(":")[0], line.substring(line.indexOf("'")+1, line.lastIndexOf("'")));
				else
					comments.put(lineNum, line);
			}
			in.close();
		} catch (IOException e) {
		}
	}

	private void rewriteConfig() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < configValues.keySet().size(); i++) {
			String key = configValues.keySet().toArray(new String[0])[i];
			sb.append(key.toUpperCase() + ": '" + configValues.get(key) + "'");
			sb.append("\n");
		}
		for (Integer commentLine : comments.keySet()) {
			sb.insert(StringUtils.ordinalIndexOf(sb.toString(), "\n", commentLine-1),  "\n" + comments.get(commentLine));
		}
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(f));
			pw.write(sb.toString());
			pw.close();
		} catch (IOException e) {
		}
	}

	public void reloadConfig() {
		readConfig();
	}

	public HashMap<String, String> getConfigValues() {
		return configValues;
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
