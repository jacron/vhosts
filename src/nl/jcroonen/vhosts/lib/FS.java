package nl.jcroonen.vhosts.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FS {
    private static String getRemoteUrl(String line) {
        String[] words = line.split("=");
        return words[1].trim()
                .replace("git@github.com:", "https://github.com/");
    }

    public static String repoRemoteUrl(String dir) {
        String p = String.format("%s/.git/config", dir);
        File file = new File(p);
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().startsWith("url")) {
                    return getRemoteUrl(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
