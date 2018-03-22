package discord.modules.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.EventDispatcher;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.modules.IModule;

public class Database implements IModule {
	private String moduleName = "Database";
	private String moduleVersion = "1.0";
	private String moduleMinimumVersion = "2.3.0";
	private String author = "SlyVitality";
	private Object handler = new DatabaseListener();
	private DatabaseConnector connector = new DatabaseConnector();
	public static IDiscordClient client;

	public boolean enable(IDiscordClient dclient) {
		client = dclient;
		EventDispatcher dispatcher = client.getDispatcher();
		dispatcher.registerListener(handler);
		System.out.println("[Module] Enabled " + moduleName + " V" + moduleVersion + " by " + author);
		return true;
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

	public DatabaseConnector getConnector() {
		return connector;
	}
	public void load(){
		connector.loadValues();
		connector.connect();
		createTables();
		//DatabaseListener.oneTimeOnly();
		fillCache();
	}
	/**
	 * Access Cache, Only access this, Never query the database for results sync
	 * Can be edited asynchronously
	 */
	private static ConcurrentHashMap<String, HashMap<String, Object>> databaseCache = new ConcurrentHashMap<String, HashMap<String, Object>>();

	public void createTables() {
		PreparedStatement createPlayers;
		// PreparedStatement createColumns;
		try {
			createPlayers = connector.getConnection()
					.prepareStatement("CREATE TABLE IF NOT EXISTS Users(" + "SNOWFLAKE varchar(50), "
							+ "NAME varchar(50), " + "DISCRIMINATOR varchar(20), " + "CREDITS int," + " PRIMARY KEY(SNOWFLAKE)" + ");");
			createPlayers.execute();
			createPlayers.close();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new DatabaseException("Failed to adjust table 'Users'");

		}

	}

	public void fillCache() {
		try {
			PreparedStatement sql;
			sql = connector.getConnection().prepareStatement("SELECT * FROM `Users`");
			ResultSet rs = sql.executeQuery();
			while (rs.next()) {
				HashMap<String, Object> toCache = new HashMap<String, Object>();
				for (int i = 0; i < UserValue.values().length; i++) {
					String name = UserValue.values()[i].name();
					if (UserValue.values()[i].type == Integer.class) {
						toCache.put(name, rs.getInt(name));
					} else if (UserValue.values()[i].type == Double.class) {
						toCache.put(name, rs.getDouble(name));
					} else {
						toCache.put(name, rs.getString(name));
					}
				}
				databaseCache.put(rs.getString("SNOWFLAKE"), toCache);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Data cache was unable to be filled from table 'Users'");
		}
	}

	public static boolean exists(String snowflake) {

		for (HashMap<String, Object> pfile : databaseCache.values()) {
			if (((String) pfile.get("SNOWFLAKE")).equalsIgnoreCase(snowflake)) {
				return true;
			}
		}
		return false;
	}

	public boolean exists(IUser u) {

		return databaseCache.containsKey(u.getID());
	}

	public void tryNewPlayer(final IUser u) {
		final String snowflake = u.getID();
		new Thread(){
			public void run() {
				if (!exists(snowflake)) {
					newPlayerData(u);
					syncDatabase(u);

				} else {
					getDatabaseCache().get(snowflake).put("NAME", u.getName());
					syncDatabase(u);
				}
			}
		}.start();;
	}

	private void newPlayerData(final IUser u) {
		final String name = u.getName();
		final String snowflake = u.getID();
		final String discriminator = u.getDiscriminator();
		new Thread(){
			public void run() {
				try {
					PreparedStatement ps2;
					String statement = "INSERT INTO `Users` values(?";
					for (int i = 0; i < UserValue.values().length-1; i++) {
						statement = statement + ",?";
					}
					statement = statement + ")";
					ps2 = connector.getConnection().prepareStatement(statement);
					ps2.setString(1, snowflake);
					for (int i = 0; i < (UserValue.values().length); i++) {
						UserValue pv = UserValue.values()[i];
						if (pv.name().equalsIgnoreCase("NAME")) {
							ps2.setString(i+1, name);
							continue;
						}
						if (pv.name().equalsIgnoreCase("DISCRIMINATOR")) {
							ps2.setString(i+1, discriminator);
							continue;
						}
						if (pv.name().equalsIgnoreCase("SNOWFLAKE")) {
							ps2.setString(i+1, snowflake);
							continue;
						}
						if (pv.initialValue instanceof Integer) {
							ps2.setInt(i+1, (Integer) pv.initialValue);
						} else if (pv.initialValue instanceof Double) {
							ps2.setDouble(i+1, (Double) pv.initialValue);
						} else {
							ps2.setString(i+1, (String) pv.initialValue);
						}
					}
					ps2.execute();
					ps2.close();
				} catch (SQLException e) {
					throw new DatabaseException("Unable to create new users row in table `User`");
				}
			}
		}.start();
		HashMap<String, Object> toCache = new HashMap<String, Object>();
		for (int i = 0; i < UserValue.values().length; i++) {
			toCache.put(UserValue.values()[i].name(), UserValue.values()[i].initialValue);
		}
		toCache.put("NAME", u.getName());
		toCache.put("DISCRIMINATOR", u.getDiscriminator());
		databaseCache.put(snowflake, toCache);
	}

	public void syncDatabase(final IUser u) {
		final String snowflake = u.getID();
		final String name = u.getName();
		new Thread(){
			public void run() {
				try {
					PreparedStatement ps2;
					String statement = "UPDATE `Users` SET";
					for (UserValue pv : UserValue.values()) {
						statement = statement + " `" + pv.name() + "`=?,";
					}
					statement = statement.substring(0, statement.lastIndexOf(',')) + " WHERE `SNOWFLAKE`=?";
					ps2 = connector.getConnection().prepareStatement(statement);
					for (int i = 0; i < UserValue.values().length; i++) {
						UserValue pv = UserValue.values()[i];
						if (pv.type == Integer.class) {
							ps2.setInt(i+1, (Integer) databaseCache.get(snowflake).get(pv.name()));
						} else if (pv.type == Double.class) {
							if (databaseCache.get(snowflake).get(pv.name()) instanceof Integer) {
								ps2.setDouble(i+1, (Integer) databaseCache.get(snowflake).get(pv.name()) + 0.0);
							} else {
								ps2.setDouble(i+1, (Double) databaseCache.get(snowflake).get(pv.name()) + 0.0);
							}
						} else {
							ps2.setString(i+1, databaseCache.get(snowflake).get(pv.name()).toString());
						}
					}
					ps2.setString(UserValue.values().length + 1, snowflake);
					ps2.executeUpdate();
					ps2.close();
				} catch (SQLException e) {
					e.printStackTrace();
					throw new DatabaseException("Unable to sync " + name + "'s data");
				}
			}
		}.start();
	}

	public static ConcurrentHashMap<String, HashMap<String, Object>> getDatabaseCache() {
		return databaseCache;
	}

	public static HashMap<String, Object> getCacheFor(IUser u) {
		return databaseCache.get(u.getID());
	}
}
