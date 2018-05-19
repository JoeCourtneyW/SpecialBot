package main.GuildOptions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Modifiable {
    String name();
    String description() default "Unconfigured";
}