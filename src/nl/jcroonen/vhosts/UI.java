package nl.jcroonen.vhosts;

import java.awt.*;
import java.io.File;

public class UI {
    public static void setDockIcon() {
        // apple only
//        String userdir = System.getProperty("user.dir"); // dit werkt niet goed in jar!
//        String iconPath = userdir + "/resources/images";
        String iconPath = "/Users/orion/Dev/java/javafx/dashboard/src/nl/jcroonen/dashboard";
        iconPath += "/dashboard.png";
        File file = new File(iconPath);
        if (file.exists()) {
            try {
                com.apple.eawt.Application application = com.apple.eawt.Application.getApplication();
                Image dockicon = Toolkit.getDefaultToolkit().getImage(iconPath);
                application.setDockIconImage(dockicon);
            } catch (RuntimeException e) {
                System.out.println("setDockIcon error:" + e);
            }
        } else {
            String msg = "Not exists: " + iconPath;
            System.out.println(msg);
        }
    }
}



