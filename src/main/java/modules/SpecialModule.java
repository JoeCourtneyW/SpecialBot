package modules;

import main.Commands.CommandExecutor;
import main.SpecialBot;

public abstract class SpecialModule {

    protected SpecialBot bot;

    public SpecialModule(SpecialBot bot) {
        this.bot = bot;
    }

    public void registerHandlers(Object... handlers) {
        for (Object handler : handlers)
            bot.getClient().getDispatcher().registerListener(handler);
    }

    public void registerCommands(CommandExecutor... executors) {
        for (CommandExecutor executor : executors)
            bot.getCommandsHandler().registerCommand(executor);
    }

    public abstract boolean enable();

    public abstract String getName();

    public abstract String getVersion();
}
