package nl.jcroonen.vhosts;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import nl.jcroonen.vhosts.lib.UI;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        String location = "vhosts.fxml";
        String fontFile = "fontawesome-webfont.ttf";
        String bundleName = "bundles.bundle";
        String cssFile = "nl/jcroonen/vhosts/DarkTheme.css";
        String appIcon = "nl/jcroonen/vhosts/dashboard.png";

        Locale locale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle(bundleName, locale);
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(bundle);
        loader.setLocation(Main.class.getResource(location));
        Font.loadFont(getClass().getResource(fontFile).toExternalForm(), 10);
        AnchorPane anchorPane = loader.load();
        Controller controller = loader.getController();
        controller.init();
        primaryStage.setTitle(bundle.getString("appname"));
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();

        Scene scene = new Scene(anchorPane,
                Double.parseDouble(bundle.getString("appwidth")),
                Double.parseDouble(bundle.getString("appheight"))
        );
        scene.getStylesheets().add(cssFile);
        primaryStage.setScene(scene);
        UI.setDockIcon();
        primaryStage.getIcons().add(new Image(appIcon));
        primaryStage.show();
        double x = bounds.getMinX() + (bounds.getWidth() - scene.getWidth()) * 0.5;
        primaryStage.setX(x);
        primaryStage.setY(10);

    }
}
