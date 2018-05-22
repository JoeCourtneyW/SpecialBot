package main.GuildOptions;

import main.Commands.Command;
import main.Commands.CommandEvent;
import main.Commands.CommandExecutor;
import utils.AnnotationUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class CommandOptions implements CommandExecutor {

    @Command(label = "options", description = "Change the guild options values for your guild")
    public void options(CommandEvent event) {
        List<Field> guildOptionFields = AnnotationUtil.getAnnotatedFields(GuildOptions.class, Modifiable.class);
        List<Modifiable> guildOptions = new ArrayList<>();
        StringJoiner optionList = new StringJoiner(", ");

        for (Field option : guildOptionFields) {
            Modifiable annotation = (Modifiable) AnnotationUtil.getAnnotation(option, Modifiable.class);
            optionList.add(annotation.name());
            guildOptions.add(annotation);
        }

        if(event.getArgs().length <= 1){
            event.reply("Options: [" + optionList.toString() + "]");
            return;
        }

        if(event.getArgs().length >= 2){
            Modifiable selectedOption = guildOptions.stream().filter(option -> option.name().equalsIgnoreCase(event.getArgs()[0])).findFirst().orElse(null);

            if(selectedOption == null){
                event.reply("Options: [" + optionList.toString() + "]");
                return;
            }

            if(validateInput(event, selectedOption)){

            }
        }

    }

    public boolean validateInput(CommandEvent event, Modifiable annotation){
        switch(annotation.validation()){
            case STRING:
                return true;
            case INTEGER:
                try{
                    Integer.parseInt(event.getArgs()[1]);
                    return true;
                } catch(Exception e) {
                    return false;
                }
            case ROLE_MENTION:
                return event.getMessage().getRoleMentions().size() > 0;
            case USER_MENTION:
                return event.getMessage().getMentions().size() > 0;
            case BOOLEAN:
                try{
                    Boolean.parseBoolean(event.getArgs()[1]);
                    return true;
                } catch(Exception e) {
                    return false;
                }
            default:
                return false;
        }
    }
}
