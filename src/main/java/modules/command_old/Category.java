package discord.modules.command;

public enum Category {

	GENERAL("General"),
	MODERATION("Mod"),
	ADMINISTRATION("Admin"),
	MUSIC("Music"),
	NONE("None");

	private String formal;
	Category(String formal){
		this.formal = formal;
	}
	public String getFormal(){
		return formal;
	}
}
