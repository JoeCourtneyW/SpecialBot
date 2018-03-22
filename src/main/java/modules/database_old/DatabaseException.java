package discord.modules.database;

public class DatabaseException extends RuntimeException{

	private static final long serialVersionUID = 1962684051029425389L;

	public DatabaseException(String message){
		super(message);
	}
}
