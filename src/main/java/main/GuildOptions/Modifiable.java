package main.GuildOptions;

public @interface Modifiable {
    //Empty interface, woo!
    String name();
    String description() default "Unconfigured";
}
