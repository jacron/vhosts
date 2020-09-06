package nl.jcroonen.vhosts;

import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import nl.jcroonen.vhosts.lib.VHosts;
import nl.jcroonen.vhosts.model.VHost;

import java.util.ResourceBundle;

public class Controller {
    @FXML private TilePane tiles;
    @FXML private ComboBox<String> sortcombobox;
    private ResourceBundle bundle;

    @FXML private void openVHostsDir() { Action.openVhostConfigsDir(); }
    @FXML private void refresh() { reinit(); }

    private VBox createTile(VHost vHost) {
        return new VBox();
    }

    private void populateTiles(ObservableList<VHost> VHosts) {
        tiles.getChildren().clear();
        for (VHost vHost : VHosts) {
            tiles.getChildren().add(createTile(vHost));
        }
    }

    private void sort(int choice) {
        try {
            ObservableList<VHost> vHosts = VHosts.getList();
//            if (choice == 1 || choice == 2) {
//                vHosts.sort((a, b) -> {
//                    if (choice == 1) {
//                        return a.getLabel().compareTo(b.getLabel());
//                    } else {
//                        return a.getPort().compareTo(b.getPort());
//                    }
//                });
//            }
            populateTiles(vHosts);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateSortbox() {
        String[] choices = {"- sort -", "Title", "Port"};
        sortcombobox.getItems().clear();
        sortcombobox.getItems().addAll(choices);
        sortcombobox.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
                    int choice = new_val.intValue();
                    sort(choice);
                });
    }

    private void reinit() {
        sortcombobox.getSelectionModel().select(1);
        sort(1);
    }

    public void init() {
        this.bundle = ResourceBundle.getBundle("bundles.bundle");
        populateSortbox();
        reinit();
    }
}
