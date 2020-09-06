package nl.jcroonen.vhosts.lib;

public class StringUtils {
    public static String trimQuotes(String s) {
        if (s.charAt(0) == '"') {
            s = s.substring(1);
        }
        if (s.endsWith("\"")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
