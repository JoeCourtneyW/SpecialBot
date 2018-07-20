package main;

public interface SpecialModule {

    SpecialBot bot = Main.bot;

    boolean onLoad();

    String getName();

    String getVersion();
}
