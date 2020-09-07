package nl.jcroonen.vhosts;

import nl.jcroonen.vhosts.lib.FS;
import nl.jcroonen.vhosts.lib.Os;

import java.util.Dictionary;
import java.util.ResourceBundle;

public class Action {
    ResourceBundle bundle;
    Dictionary<String, String> dictionary;

    public static void openVhostConfigsDir() {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.bundle");
        String[] args = {"open", bundle.getString("vhostconfigsdir")};
        Os.execute(args);
    }

    public Action(Dictionary<String, String> dictionary) {
        bundle = ResourceBundle.getBundle("bundles.bundle");
        this.dictionary = dictionary;
    }

    private String getRoot() {
        if (dictionary.get("DocumentRoor") != null) {
            return dictionary.get("DocumentRoot");
        } else {
            return dictionary.get("Directory");
        }
    }

    public void editVhostConfig() {
        String q = getRoot() + "/vhost.config";
        String[] args = {bundle.getString("editorPath"), q};
        Os.execute(args);
    }

    public void editConfigFile() {
        String[] args = {bundle.getString("editorPath"), dictionary.get("File")};
        Os.execute(args);
    }

    public void openInFinder() {
        String[] args = {"open", getRoot()};
        Os.execute(args);
    }

    public void openErrorOutput() {
        String errorOutput = dictionary.get("ErrorLog");
        if (errorOutput != null) {
            String[] args = {bundle.getString("editorPath"), errorOutput};
            Os.execute(args);
        }
    }

    public void github() {
        String q = FS.repoRemoteUrl(getRoot());
        String[] args = {"open", q};
        Os.execute(args);
    }

    public void openIde(String key) {
        String[] args = {bundle.getString(key), getRoot()};
        Os.execute(args);
    }

    public void openServiceDefaultClient() {
        String url = String.format("http://%s", dictionary.get("ServerName"));
        String port = dictionary.get("VirtualHost");
        if (!port.equals("*:80")) {
            url += port.substring(1);
        }
        String[] args = {"open", url};
        Os.execute(args);
    }//http://avcat/

}
