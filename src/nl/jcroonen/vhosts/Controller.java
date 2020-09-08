package nl.jcroonen.vhosts;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.TilePane;
import nl.jcroonen.vhosts.lib.VHosts;
import nl.jcroonen.vhosts.model.VHost;

import static nl.jcroonen.vhosts.lib.VHosts.getSortedVHosts;

public class Controller {
    @FXML private TilePane tiles;
    @FXML private TextField filter;
    @FXML private Label filterClear;
    @FXML private void openVHostsDir() { Action.openVhostConfigsDir(); }
    @FXML private void refresh() { reinit(); }
    @FXML private void onFilterAction() { onFilterChange(false); }
    @FXML private void onFilterClear() { onFilterChange(true); }
    @FXML private void onHosts() { editHosts(); }

    ObservableList<VHost> vHosts;

    private void editHosts() {
        VHosts.openHosts();
    }

    private String changeFilter(Boolean clear) {
        if (clear) {
            filter.setText("");
        }
        return filter.getText();
    }

    private void setClear(int len) {
        filterClear.setText(len == 0 ? "" : "x");
    }

    private void populateTiles(String filter) {
        TileBuilder tileBuilder = new TileBuilder();
        tileBuilder.populateTiles(filter, tiles, vHosts);
    }

    private void onFilterChange(Boolean clear) {
        String filterText = changeFilter(clear);
        setClear(filterText.length());
        populateTiles(filterText);
    }

    private void reinit() {
        ObservableList<VHost> vHosts = getSortedVHosts();
        if (vHosts != null) {
            this.vHosts = vHosts;
            populateTiles(null);
        }
    }

    public void init() {
        reinit();
        filterClear.setText("");
    }
}
