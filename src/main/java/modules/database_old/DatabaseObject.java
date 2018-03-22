package discord.modules.database;

public class DatabaseObject {

	Object o;

	public DatabaseObject(Object o) {
		this.o = o;
	}

	public int asInt() {
		return (Integer) o;
	}

	public double asDouble() {
		return (Double) o;
	}

	public String asString() {
		return o.toString();
	}

	public String[] asStringArray() {
			return o.toString().split(":");
	}
}
