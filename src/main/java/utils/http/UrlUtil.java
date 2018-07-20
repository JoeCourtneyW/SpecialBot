package utils.http;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class UrlUtil {

    private static Pattern youtubeRegex;

    static {
        youtubeRegex = Pattern.compile("^(https?://)?(www\\.)?(youtube\\.com|youtu\\.?be)/.+$");
    }

    public static boolean isUrl(String verify) {
        try {
            new URL(verify).toURI();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            return false;
        }
    }

    public static boolean isYoutubeURL(String url) {
        return youtubeRegex.matcher(url).matches();
    }

    public static URL getUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String encodeURIComponent(String s) {
        String result;

        try {
            result = URLEncoder.encode(s, "UTF-8")
                    .replaceAll("\\+", "%20")
                    .replaceAll("%21", "!")
                    .replaceAll("%27", "'")
                    .replaceAll("%28", "(")
                    .replaceAll("%29", ")")
                    .replaceAll("%7E", "~");
        } catch (UnsupportedEncodingException e) {
            result = s;
        }

        return result;
    }
}
