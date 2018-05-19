package main.GuildOptions;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import utils.AnnotationUtil;

import java.lang.reflect.Field;
import java.util.List;

public class CommandOptions implements CommandExecutor {

    @Command(label = "options", description = "Change the guild options values for your guild")
    public void options(CommandEvent event) {
        Field option;
    }

    public static void main(String[] args) {
        List<Field> guildOptions = AnnotationUtil.getAnnotatedFields(GuildOptions.class, Modifiable.class);
        for (Field option : guildOptions) {

        }
    }
}
