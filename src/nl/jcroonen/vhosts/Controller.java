package nl.jcroonen.vhosts;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import nl.jcroonen.vhosts.lib.DecodeIcon;
import nl.jcroonen.vhosts.lib.FS;
import nl.jcroonen.vhosts.lib.VHosts;
import nl.jcroonen.vhosts.model.VHost;

import java.util.Dictionary;

public class Controller {
    @FXML private TilePane tiles;
    @FXML private TextField filter;
    @FXML private Label filterClear;
    @FXML private void openVHostsDir() { Action.openVhostConfigsDir(); }
    @FXML private void refresh() { reinit(); }
    @FXML private void onFilterAction() { onFilterChange(false); }
    @FXML private void onFilterClear() { onFilterChange(true); }
    @FXML private void onHosts() { hosts(); }

    ObservableList<VHost> vHosts;

    private String getRoot(Dictionary<String, String> dictionary) {
        if (dictionary.get("DocumentRoor") != null) {
            return dictionary.get("DocumentRoot");
        } else {
            return dictionary.get("Directory");
        }
    }

    private Label createMetaLabel(Dictionary<String, String> dictionary) {
        String description = dictionary.get("description");
        Label label = new Label(description);
        label.setPrefWidth(230);
        label.setWrapText(true);
        return label;
    }

    private Label createNameLabel(Dictionary<String, String> dictionary,
                                  Dictionary<String, String> metaDictionary) {
        Label label = new Label(dictionary.get("ServerName"));
        label.getStyleClass().add("tile-title");
        String port = dictionary.get("VirtualHost");
        if (!port.equals("*:80")) {
            label.setText(label.getText() + port.substring(1));
        }
        if (FS.exists(getRoot(dictionary))) {
            label.getStyleClass().add("active");
            DecodeIcon decodeIcon = new DecodeIcon();
            decodeIcon.tryFavicons(label, getRoot(dictionary));
//            tryFavicons(label, getRoot(dictionary));
        }
        VhostContextMenuCreator vhostContextMenuCreator = new VhostContextMenuCreator();
        label.setContextMenu(vhostContextMenuCreator.createContextMenu(dictionary, metaDictionary));
        return label;
    }

    private VBox createTile(Dictionary<String, String> dictionary) {
        VBox tile = new VBox(8);
        Dictionary<String, String> metaDictionary = FS.metaVhost(getRoot(dictionary));
        tile.getChildren().addAll(
                createNameLabel(dictionary, metaDictionary),
                createMetaLabel(metaDictionary));
        tile.setPadding(new Insets(0, 0, 12, 0));
        tile.setPrefWidth(240);
        return tile;
    }

    private void populateTiles(ObservableList<VHost> VHosts, String filter) {
        tiles.getChildren().clear();
        for (VHost vHost : VHosts) {
            if (filter == null || vHost.getDictionary().get("ServerName").contains(filter)) {
                tiles.getChildren().add(createTile(vHost.getDictionary()));
            }
        }
    }

    private void sort() {
        try {
            vHosts = VHosts.getList();
            vHosts.sort((a, b) -> {
                    String nameA = a.getDictionary().get("ServerName");
                    String nameB = b.getDictionary().get("ServerName");
                    return nameA.compareTo(nameB);
            });
            populateTiles(vHosts, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hosts() {
        VHosts.openHosts();
    }

    private void onFilterChange(Boolean clear) {
//        System.out.println(filter.getText());
        String filterText;
        if (clear) {
            filter.setText("");
            filterText = "";
        } else {
            filterText = filter.getText();
        }
        if (filterText.length() == 0) {
            filterClear.setText("");
        } else {
            filterClear.setText("x");
        }
        populateTiles(vHosts, filterText);
    }

    private void reinit() {
        sort();
    }

    public void init() {
        reinit();
        filterClear.setText("");
    }
}
