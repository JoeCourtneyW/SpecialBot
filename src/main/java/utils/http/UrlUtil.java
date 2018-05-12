package utils.http;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.regex.Pattern;

public class UrlUtil {
    public static boolean isUrl(String verify) {
        try {
            new URL(verify).toURI();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
    }

    public static boolean isYoutubeURL(String url) {
        return Pattern.matches("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$", url);
    }

    public static URL getUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
