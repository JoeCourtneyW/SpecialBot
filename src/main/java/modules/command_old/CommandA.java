package discord.modules.command;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public  @interface CommandA {

	 Category category() default Category.GENERAL;
	
	 String label();
	
	 String name();
	
	 PermissionLevel permissionLevel() default PermissionLevel.USER;
	
	 String usage() default "Unconfigured Command";
	
	 String description() default "Unconfigured Command";
	 
	 String alias() default "";
}
