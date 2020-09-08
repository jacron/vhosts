package nl.jcroonen.vhosts;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import nl.jcroonen.vhosts.lib.DecodeIcon;
import nl.jcroonen.vhosts.lib.FS;
import nl.jcroonen.vhosts.model.VHost;

import java.util.Dictionary;

public class TileBuilder {
    private Label createMetaLabel(Dictionary<String, String> dictionary) {
        String description = dictionary.get("description");
        Label label = new Label(description);
        label.setPrefWidth(230);
        label.setWrapText(true);
        return label;
    }

    private Label createNameLabel(Dictionary<String, String> dictionary,
                                  Dictionary<String, String> metaDictionary,
                                  String root) {
        Label label = new Label(dictionary.get("ServerName"));
        label.getStyleClass().add("tile-title");
        String port = dictionary.get("VirtualHost");
        if (!port.equals("*:80")) {
            label.setText(label.getText() + port.substring(1));
        }
        if (FS.exists(root)) {
            label.getStyleClass().add("active");
            DecodeIcon decodeIcon = new DecodeIcon();
            decodeIcon.tryFavicons(label, root);
        }
        VhostContextMenuCreator vhostContextMenuCreator = new VhostContextMenuCreator();
        label.setContextMenu(vhostContextMenuCreator.createContextMenu(dictionary, metaDictionary));
        return label;
    }

    private VBox createTile(VHost vHost) {
        VBox tile = new VBox(8);
        Dictionary<String, String> dictionary = vHost.getDictionary();
        String root = vHost.getRoot();
        Dictionary<String, String> metaDictionary = FS.metaVhost(root);
        tile.getChildren().addAll(
                createNameLabel(dictionary, metaDictionary, root),
                createMetaLabel(metaDictionary));
        tile.setPadding(new Insets(0, 0, 12, 0));
        tile.setPrefWidth(240);
        return tile;
    }

    public void populateTiles(String filter, TilePane tilePane,
                              ObservableList<VHost> vHosts) {
        tilePane.getChildren().clear();
        for (VHost vHost : vHosts) {
            if (filter == null || vHost.getDictionary().get("ServerName").contains(filter)) {
                tilePane.getChildren().add(createTile(vHost));
            }
        }
    }
}
