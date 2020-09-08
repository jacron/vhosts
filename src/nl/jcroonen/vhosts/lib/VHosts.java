package nl.jcroonen.vhosts.lib;

import stackify.ApacheConfigParser;
import stackify.ConfigNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.jcroonen.vhosts.model.VHost;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ResourceBundle;

public class VHosts {

    private static ArrayList<String> getFiles() {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.bundle");
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            Files.list(new File(bundle.getString("vhostconfigsdir")).toPath())
                    .forEach(path -> {
                        String s = path.toString();
                        if (s.endsWith(".conf")) {
                            arrayList.add(s);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arrayList;
    }

    private static ConfigNode getConfig(String q) {
        File file = new File(q);
        try {
            InputStream inputStream = new FileInputStream(file);
            ApacheConfigParser parser = new ApacheConfigParser();
            return parser.parse(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void addToDict(Dictionary<String, String> dictionary, ConfigNode configNode, String[] keys) {
        for (String key : keys) {
            if (configNode.getName().equals(key)) {
                dictionary.put(key, StringUtils.trimQuotes(configNode.getContent().trim()));
            }
        }
    }

    private static VHost createVHost(ConfigNode configNode, String file) {
        Dictionary<String, String> dictionary = new Hashtable<>();
        dictionary.put("File", file);
        for (ConfigNode child : configNode.getChildren()) {
            if (child.getName().equals("VirtualHost")) {
                dictionary.put("VirtualHost", child.getContent());
                for (ConfigNode grandchild : child.getChildren()) {
                    String[] keys = {"ServerName", "DocumentRoot", "ErrorLog", "Directory"};
                    addToDict(dictionary, grandchild, keys);
                }
            }
        }
        return new VHost(dictionary);
    }

    private static ArrayList<VHost> getVhosts(ArrayList<String> files) {
        ArrayList<VHost> vHosts = new ArrayList<>();
        for (String file : files) {
            ConfigNode configNode = getConfig(file);
            if (configNode != null) {
                vHosts.add(createVHost(configNode, file));
            }
        }
        return vHosts;
    }

    private static ObservableList<VHost> getList() {
        ArrayList<String> files = getFiles();
        ArrayList<VHost> vhosts = getVhosts(files);
        ObservableList<VHost> observableVHosts = FXCollections.observableArrayList();
        observableVHosts.addAll(vhosts);
        return observableVHosts;
    }

    public static ObservableList<VHost> getSortedVHosts() {
        ObservableList<VHost> vHosts = null;
        try {
            vHosts = getList();
            vHosts.sort((a, b) -> {
                String nameA = a.getDictionary().get("ServerName");
                String nameB = b.getDictionary().get("ServerName");
                return nameA.compareTo(nameB);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vHosts;
    }

    public static void openHosts() {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.bundle");
        String[] args = {
                bundle.getString("editorPath"),
                bundle.getString("hostsPath")
        };
        Os.execute(args);
    }
}
