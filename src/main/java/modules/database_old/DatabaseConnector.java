package discord.modules.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import discord.Main;

public class DatabaseConnector {

	private String HOSTNAME, /*PORT,*/ DATABASE, USERNAME, PASSWORD;
	public Connection connection = null;

	public DatabaseConnector(){
	}
	public void loadValues() {
		//PORT = "3306";
		HOSTNAME = "127.0.0.1";
		DATABASE = "cynical_bot";
		USERNAME = Main.DATABASE_USERNAME;
		PASSWORD = Main.DATABASE_PASSWORD;
		System.out.println("Connecting to " + HOSTNAME + "/" + DATABASE + " with username: " + USERNAME + " and password: " + PASSWORD);
	}

	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + HOSTNAME + "/" + DATABASE, USERNAME,
					PASSWORD);
			System.out.println("Database successfully connected!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		if (connection != null) {
			return connection;
		} else {
			throw new DatabaseException("Connection was referenced as null");
		}
	}

	public void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else {
			throw new DatabaseException("No connection available to close");
		}
	}
}
