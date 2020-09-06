package nl.jcroonen.vhosts;

import java.util.ResourceBundle;

public class Action {
    public static void openVhostConfigsDir() {
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.bundle");
        String[] args = {"open", bundle.getString("vhostconfigsdir")};
        Os.execute(args);
    }

}
