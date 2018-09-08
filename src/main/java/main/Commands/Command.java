package main.Commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {

    String label();

    PermissionLevel permissionLevel() default PermissionLevel.DEFAULT;

    String usage() default ".command";

    String description() default "This command does not yet have a description";

    String alias() default "";

    boolean guildAdminOnly() default false;
}
