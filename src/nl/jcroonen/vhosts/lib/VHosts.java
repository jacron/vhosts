package nl.jcroonen.vhosts.lib;

import apache.ApacheConfigParser;
import apache.ConfigNode;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import nl.jcroonen.vhosts.model.VHost;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
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

    private static ConfigNode getPlist(String q) {
        File file = new File(q);
        try {
            InputStream inputStream = new FileInputStream(file);
            ApacheConfigParser parser = new ApacheConfigParser();
            return parser.parse(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static VHost createVHost(ConfigNode configNode) {
        return new VHost();
    }

    private static ArrayList<VHost> getVhosts(ArrayList<String> files) {
        ArrayList<VHost> vHosts = new ArrayList<>();
        for (String file : files) {
            ConfigNode configNode = getPlist(file);
            vHosts.add(createVHost(configNode));
        }
        return null;
    }

    public static ObservableList<VHost> getList() {
        ArrayList<String> files = getFiles();
        ArrayList<VHost> vhosts = getVhosts(files);
        ObservableList<VHost> observableLaunchAgents = FXCollections.observableArrayList();
        observableLaunchAgents.addAll(vhosts);
        return observableLaunchAgents;
    }
}
