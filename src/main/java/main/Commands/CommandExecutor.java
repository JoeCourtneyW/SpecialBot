package main.Commands;

import main.SpecialBot;

public abstract class CommandExecutor {
    protected SpecialBot bot;
    public CommandExecutor(SpecialBot bot){
        this.bot = bot;
    }
}
