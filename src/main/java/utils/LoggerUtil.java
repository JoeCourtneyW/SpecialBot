package utils;

public class LoggerUtil {

    public static void CRITICAL(String message){
        PLAIN("[CRITICAL] " + message);
    }

    public static void WARNING(String message){
        PLAIN("[WARNING] " + message);
    }

    public static void INFO(String message){
        PLAIN("[INFO] " + message);
    }

    public static void DEBUG(String message){
        PLAIN("[DEBUG] " + message);
    }

    public static void PLAIN(String message){
        System.out.println(message);
    }
}
