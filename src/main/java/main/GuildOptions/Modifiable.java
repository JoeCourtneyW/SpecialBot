package main.GuildOptions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Modifiable {
    String name();
    String description() default "Unconfigured";
    InputType validation();

    enum InputType {
        STRING, BOOLEAN, INTEGER, USER_MENTION, ROLE_MENTION
    }
}
