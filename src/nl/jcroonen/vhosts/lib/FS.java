package nl.jcroonen.vhosts.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

public class FS {
    private static String getRemoteUrl(String line) {
        String[] words = line.split("=");
        return words[1].trim()
                .replace("git@github.com:", "https://github.com/");
    }

    /**
     * Read .git/config and find the remote url.
     * @param dir String
     * @return String
     */
    public static String repoRemoteUrl(String dir) {
        String result = null;
        String p = String.format("%s/.git/config", dir);
        File file = new File(p);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().startsWith("url")) {
                    result = getRemoteUrl(line);
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static Dictionary<String, String> metaVhost(String dir) {
        String p = String.format("%s/vhost.config", dir);
        Dictionary<String, String> dictionary = new Hashtable<>();
        try {///Users/orion/Dev/python/booklibrary_flask/vhost.config
            File file = new File(p);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            String section = null;
            StringBuilder content = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().startsWith("[")) {
                    if (section != null && content != null) {
                        dictionary.put(section, content.toString());
                    }
                    content = null;
                    section = StringUtils.trimSquareBrackets(line.trim());
                } else if (section != null) {
                    if (content != null) content.append("\n");
                    else content = new StringBuilder();
                    content.append(line.trim());
                }
            }
            if (section != null && content != null) {
                dictionary.put(section, content.toString());
            }
        } catch (IOException e) {
//            System.out.println(e.getMessage());
        }
        return dictionary;
    }

    public static boolean exists(String root) {
        File file = new File(root);
        return file.exists();
    }
}
